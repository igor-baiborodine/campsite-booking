"""Exercise the real shell/report owners with disposable executable Maven/Java adapters."""

import os
import shutil
import signal
import subprocess
import sys
from pathlib import Path

import pytest

pytestmark = pytest.mark.integration
SOURCE = Path(__file__).resolve().parents[1]


@pytest.fixture
def runner(consumer: Path) -> tuple[Path, dict[str, str]]:
    for name in ("java.sh", "test_reports.py"):
        shutil.copy2(SOURCE / name, consumer / "scripts/ai" / name)
    runtime = consumer / "ai/framework/scripts/runtime_check.sh"
    runtime.parent.mkdir(parents=True)
    shutil.copy2(SOURCE.parents[1] / "ai/framework/scripts/runtime_check.sh", runtime)
    fixture = consumer / "scripts/ai/fixtures/FrameworkPilotIT.java"
    fixture.parent.mkdir()
    fixture.write_text("// owned synthetic fixture\n")
    (consumer / "campsite-booking-service/src/test/java/com/kiroule/campsitebooking").mkdir(
        parents=True
    )
    (consumer / "settings.xml").write_text("<settings/>\n")
    binaries = consumer / "bin"
    binaries.mkdir()
    java = binaries / "java"
    java.write_text("#!/bin/sh\nprintf 'openjdk version \"17.0.1\"\\n' >&2\n")
    java.chmod(0o755)
    maven = binaries / "mvn"
    maven.write_text(
        f"#!{sys.executable}\n"
        + """import os, sys
from pathlib import Path
root = Path.cwd()
with (root / "calls").open("a") as log:
    log.write("version\\n" if "-version" in sys.argv else "build\\n")
if "-version" in sys.argv:
    print("Apache Maven 3.9.9 (fixture)")
    print("Java version: " + os.environ.get("RUNNER_TEST_JDK", "17.0.1") + ", vendor: fixture")
    sys.exit(0)
mode = os.environ.get("RUNNER_TEST_MODE", "passed")
if mode == "maven-failed": sys.exit(5)
if mode == "missing": sys.exit(0)
suites = [("surefire", "ExampleTest")]
if "verify" in sys.argv:
    suites.append(("failsafe", "PilotIT" if "-Dit.test=FrameworkPilotIT" in sys.argv else "ExampleIT"))
for kind, identity in suites:
    directory = root / "campsite-booking-service/target" / (kind + "-reports")
    directory.mkdir(parents=True, exist_ok=True)
    name = "omitted_original" if mode == "omitted" else "happy_path"
    child = "<failure/>" if mode == "ignored-failure" else "<skipped/>" if mode == "skipped" else ""
    report = directory / "TEST-Example.xml"
    report.write_text('<testsuite tests="1" failures="0" errors="0" skipped="0">'
        + '<testcase classname="' + identity + '" name="' + name + '">' + child
        + '</testcase></testsuite>')
    if mode == "stale": os.utime(report, ns=(1, 1))
"""
    )
    maven.chmod(0o755)
    environment = {
        "PATH": f"{binaries}:/usr/bin:/bin",
        "PYTHON": sys.executable,
        "AI_MAVEN_SETTINGS": str(consumer / "settings.xml"),
    }
    return consumer, environment


def invoke(runner: tuple[Path, dict[str, str]], gate: str) -> subprocess.CompletedProcess[str]:
    root, environment = runner
    with subprocess.Popen(
        [str(root / "scripts/ai/java.sh"), gate],
        cwd=root / "scripts",  # Root resolution must not depend on the caller's cwd.
        env=environment,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        start_new_session=True,
    ) as process:
        try:
            stdout, stderr = process.communicate(timeout=15)
        except BaseException:
            try:
                os.killpg(process.pid, signal.SIGKILL)
            except ProcessLookupError:
                pass
            process.communicate(timeout=5)
            raise
        return subprocess.CompletedProcess(process.args, process.returncode, stdout, stderr)


@pytest.mark.parametrize("gate", ["unit", "integration", "behavior"])
def test_given_matching_runtime_and_reports_when_run_then_pass_and_cleanup(runner, gate):
    # when
    result = invoke(runner, gate)
    # then
    assert result.returncode == 0, result.stderr
    assert '"test_reports": "passed"' in result.stdout
    root, _ = runner
    assert (root / "calls").read_text() == "version\nbuild\n"
    assert not (
        root
        / "campsite-booking-service/src/test/java/com/kiroule/campsitebooking/FrameworkPilotIT.java"
    ).exists()


@pytest.mark.parametrize("mode", ["missing", "skipped", "ignored-failure", "stale", "omitted"])
def test_given_successful_maven_with_invalid_reports_when_run_then_gate_fails(runner, mode):
    # given
    root, environment = runner
    environment["RUNNER_TEST_MODE"] = mode
    # when
    result = invoke(runner, "behavior")
    # then
    assert result.returncode == 1
    assert "test-reports:" in result.stderr
    assert not (
        root
        / "campsite-booking-service/src/test/java/com/kiroule/campsitebooking/FrameworkPilotIT.java"
    ).exists()


@pytest.mark.parametrize("name", ["MAVEN_ARGS", "MAVEN_OPTS", "JAVA_TOOL_OPTIONS"])
def test_given_inherited_skip_when_run_then_stops_before_maven(runner, name):
    # given
    root, environment = runner
    environment[name] = "-DskipTests=true -Dsecret=synthetic-sentinel"
    # when
    result = invoke(runner, "unit")
    # then
    assert result.returncode == 1
    assert not (root / "calls").exists()
    assert "synthetic-sentinel" not in result.stderr + result.stdout


def test_given_maven_jdk_mismatch_when_run_then_stops_before_build(runner):
    # given
    root, environment = runner
    environment["RUNNER_TEST_JDK"] = "14.0.2"
    # when
    result = invoke(runner, "unit")
    # then
    assert result.returncode == 1
    assert "Maven must run on Java 17" in result.stderr
    assert (root / "calls").read_text() == "version\n"


def test_given_maven_failure_when_run_then_original_nonzero_propagates(runner):
    # given
    _, environment = runner
    environment["RUNNER_TEST_MODE"] = "maven-failed"
    # when
    result = invoke(runner, "unit")
    # then
    assert result.returncode == 5
    assert '"test_reports": "passed"' not in result.stdout
