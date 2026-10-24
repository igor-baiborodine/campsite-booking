"""Independent expectations for report validation and safe option rejection."""

import json
import os
import time
from pathlib import Path

import pytest

from test_reports import GateError, expected_cases, preflight, verify


def test_given_complete_fresh_report_when_checked_then_unit_passes(consumer: Path, report: Path):
    # given
    original = report.read_bytes()
    # when
    result = verify(consumer, "unit", 1)
    # then
    assert result == {"unit": 1}
    assert report.read_bytes() == original


@pytest.mark.parametrize("result", ["skipped", "failure", "error"])
def test_given_nonpassing_case_when_checked_then_rejected(
    consumer: Path, report: Path, result: str
):
    # given: deliberately leave suite counters at zero to challenge individual case inspection.
    report.write_text(
        report.read_text().replace(" /></testsuite>", f"><{result}/></testcase></testsuite>")
    )
    # when / then
    with pytest.raises(GateError, match="test case"):
        verify(consumer, "unit", 1)


@pytest.mark.parametrize(
    ("body", "message"),
    [
        ('<testsuite tests="0" failures="0" errors="0" skipped="0"/>', "1 missing"),
        ('<testsuite tests="2" failures="0" errors="0" skipped="0"/>', "counters"),
        ('<testsuite tests="0" failures="1" errors="0" skipped="0"/>', "failures"),
        ("<testsuite", "malformed"),
        ('<!DOCTYPE testsuite [<!ENTITY data "sentinel">]><testsuite/>', "entities"),
    ],
)
def test_given_invalid_report_when_checked_then_rejected(
    consumer: Path, report: Path, body: str, message: str
):
    # given
    report.write_text(body)
    # when / then
    with pytest.raises(GateError, match=message):
        verify(consumer, "unit", 1)


@pytest.mark.parametrize("mutation", ["missing", "stale", "duplicate", "renamed", "symlink"])
def test_given_discovery_or_freshness_defect_when_checked_then_rejected(
    consumer: Path, report: Path, mutation: str
):
    # given
    started = 1
    if mutation == "missing":
        report.unlink()
    elif mutation == "stale":
        os.utime(report, ns=(1, 1))
        started = time.time_ns()
    elif mutation == "duplicate":
        report.with_name("TEST-Duplicate.xml").write_bytes(report.read_bytes())
    elif mutation == "renamed":
        report.write_text(report.read_text().replace("happy_path", "different_case"))
    else:
        original = report.with_suffix(".saved")
        report.rename(original)
        report.symlink_to(original)
    # when / then
    with pytest.raises(GateError):
        verify(consumer, "unit", started)


@pytest.mark.parametrize(
    ("name", "value"),
    [
        ("MAVEN_ARGS", "-DskipTests=true -Dsecret=synthetic-sentinel"),
        ("MAVEN_OPTS", "-Dmaven.test.failure.ignore=true"),
        ("JAVA_TOOL_OPTIONS", "-Dmaven.test.skip=true"),
        ("JDK_JAVA_OPTIONS", "-DskipITs=true"),
        ("_JAVA_OPTIONS", "-Dmaven.clean.skip=true"),
        ("MAVEN_OPTS", '-D "skipTests=true"'),
    ],
)
def test_given_inherited_override_when_preflight_then_rejected_without_values(
    consumer: Path, name: str, value: str
):
    # when / then
    with pytest.raises(GateError) as result:
        preflight(consumer, {name: value})
    # then
    assert name in str(result.value)
    assert value not in str(result.value)
    assert "synthetic-sentinel" not in str(result.value)


@pytest.mark.parametrize("name", ["maven.config", "jvm.config"])
def test_given_project_override_when_preflight_then_rejected(consumer: Path, name: str):
    # given
    (consumer / ".mvn").mkdir()
    (consumer / ".mvn" / name).write_text("-DskipTests=true\n")
    # when / then
    with pytest.raises(GateError, match="overrides"):
        preflight(consumer, {})


def test_given_memory_options_when_preflight_then_preserved_and_accepted(consumer: Path):
    # given
    environment = {"MAVEN_OPTS": "-Xmx512m"}
    before = time.time_ns()
    # when
    result = preflight(consumer, environment)
    # then
    assert before <= result <= time.time_ns()
    assert environment == {"MAVEN_OPTS": "-Xmx512m"}


def test_given_duplicate_baseline_when_loaded_then_rejected(consumer: Path):
    # given
    path = consumer / "scripts/ai/expected-test-cases.json"
    data = json.loads(path.read_text())
    data["suites"]["unit"] *= 2
    path.write_text(json.dumps(data))
    # when / then
    with pytest.raises(GateError, match="independent baseline"):
        expected_cases(consumer)
