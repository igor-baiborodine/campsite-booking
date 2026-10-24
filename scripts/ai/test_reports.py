"""Check fixed Maven gate inputs and independently recorded test discovery."""

from __future__ import annotations

import argparse
import json
import os
import shlex
import signal
import sys
import time
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

GATES = {
    "unit": ("unit",),
    "integration": ("unit", "integration"),
    "behavior": ("unit", "behavior"),
}
PROTECTED_PROPERTIES = {
    "skipTests",
    "skipITs",
    "maven.test.skip",
    "maven.test.failure.ignore",
    "maven.clean.skip",
}


class GateError(Exception):
    """A named gate cannot establish its required execution."""


def check_options(value: str, source: str) -> None:
    """Reject overrides without echoing potentially secret-bearing option values."""
    try:
        tokens = shlex.split(value)
    except ValueError as error:
        raise GateError(f"invalid options in {source}") from error
    for index, token in enumerate(tokens):
        if token == "-D":
            token += tokens[index + 1] if index + 1 < len(tokens) else ""
        if token.startswith("-D") and token[2:].split("=", 1)[0] in PROTECTED_PROPERTIES:
            raise GateError(f"test/clean overrides are unsupported in {source}")


def preflight(root: Path, environment: dict[str, str]) -> int:
    """Validate before Maven or fixture writes; return the report freshness boundary."""
    if environment.get("MAVEN_ARGS", "").strip():
        raise GateError("MAVEN_ARGS must be unset for fixed test gates")
    for name in ("MAVEN_OPTS", "JAVA_TOOL_OPTIONS", "JDK_JAVA_OPTIONS", "_JAVA_OPTIONS"):
        check_options(environment.get(name, ""), name)
    for name in ("maven.config", "jvm.config"):
        path = root / ".mvn" / name
        if path.exists():
            check_options(path.read_text(), f".mvn/{name}")
    expected_cases(root)  # A broken baseline must fail before clean/test effects.
    return time.time_ns()


def expected_cases(root: Path) -> dict[str, set[tuple[str, str]]]:
    path = root / "scripts/ai/expected-test-cases.json"
    try:
        data = json.loads(path.read_text())
        if data["version"] != 1 or set(data["suites"]) != {"unit", "integration", "behavior"}:
            raise ValueError("unsupported manifest")
        expected = {}
        for name, cases in data["suites"].items():
            if not isinstance(cases, list) or not cases:
                raise ValueError("empty case list")
            for case in cases:
                if not isinstance(case, list) or len(case) != 2:
                    raise ValueError("invalid case")
                if not all(isinstance(value, str) and value for value in case):
                    raise ValueError("invalid identity")
            identities = {tuple(case) for case in cases}
            if len(identities) != len(cases):
                raise ValueError("duplicate identity")
            expected[name] = identities
        return expected
    except (OSError, ValueError, KeyError, TypeError) as error:
        raise GateError(
            "invalid expected-test-cases.json; review the independent baseline"
        ) from error


def report_cases(path: Path, started_ns: int) -> list[tuple[str, str]]:
    if path.is_symlink() or path.stat().st_mtime_ns < started_ns:
        raise GateError("stale or redirected test report")
    if path.stat().st_size > 16 * 1024 * 1024:
        raise GateError("test report exceeds 16 MiB")
    content = path.read_bytes()
    if b"<!DOCTYPE" in content or b"<!ENTITY" in content:
        raise GateError("test reports must not declare XML entities")
    try:
        suite = ET.fromstring(content)
        cases = suite.findall("testcase")
        if suite.tag != "testsuite" or int(suite.attrib["tests"]) != len(cases):
            raise ValueError("invalid suite count")
        if any(int(suite.attrib[key]) != 0 for key in ("failures", "errors", "skipped")):
            raise GateError("test report contains failures, errors or skips")
        identities = []
        for case in cases:
            if any(case.find(kind) is not None for kind in ("failure", "error", "skipped")):
                raise GateError("test case contains a failure, error or skip")
            identity = (case.attrib["classname"], case.attrib["name"])
            if not all(identity):
                raise ValueError("missing case identity")
            identities.append(identity)
        return identities
    except (ET.ParseError, KeyError, ValueError) as error:
        raise GateError("malformed test report or inconsistent suite counters") from error


def verify(root: Path, gate: str, started_ns: int) -> dict[str, int]:
    if started_ns <= 0 or started_ns > time.time_ns():
        raise GateError("invalid test-start timestamp")
    expected = expected_cases(root)
    result = {}
    for name in GATES[gate]:
        owner = "surefire-reports" if name == "unit" else "failsafe-reports"
        directory = root / "campsite-booking-service/target" / owner
        if directory.resolve() != directory.absolute():
            raise GateError("redirected test report directory")
        paths = sorted(directory.glob("TEST-*.xml"))
        if not paths:
            raise GateError(f"missing {name} test reports")
        actual = Counter(case for path in paths for case in report_cases(path, started_ns))
        if any(count != 1 for count in actual.values()):
            raise GateError(f"duplicate {name} test identities")
        missing, unexpected = expected[name] - actual.keys(), actual.keys() - expected[name]
        if missing or unexpected:
            raise GateError(
                f"{name} discovery differs: {len(missing)} missing, {len(unexpected)} unexpected; "
                "review expected-test-cases.json alongside intentional test changes"
            )
        result[name] = len(actual)
    return result


def terminated(_signum: int, _frame: object) -> None:
    raise SystemExit(143)


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    commands.add_parser("preflight")
    check = commands.add_parser("verify")
    check.add_argument("gate", choices=GATES)
    check.add_argument("started_ns", type=int)
    args = parser.parse_args(argv)
    root = Path(__file__).resolve().parents[2]
    try:
        if args.command == "preflight":
            print(preflight(root, dict(os.environ)))
        else:
            print(
                json.dumps(
                    {"test_reports": "passed", "counts": verify(root, args.gate, args.started_ns)}
                )
            )
        return 0
    except (GateError, OSError) as error:
        message = (
            str(error) if isinstance(error, GateError) else "cannot read test gate inputs/reports"
        )
        print(f"test-reports: {message}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    signal.signal(signal.SIGTERM, terminated)
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        sys.exit(130)
