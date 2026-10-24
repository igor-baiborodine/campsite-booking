"""Small independent report and disposable consumer fixtures."""

import json
from pathlib import Path

import pytest


@pytest.fixture
def consumer(tmp_path: Path) -> Path:
    directory = tmp_path / "consumer"
    owner = directory / "scripts/ai"
    owner.mkdir(parents=True)
    (owner / "expected-test-cases.json").write_text(
        json.dumps(
            {
                "version": 1,
                "suites": {
                    "unit": [["ExampleTest", "happy_path"]],
                    "integration": [["ExampleIT", "happy_path"]],
                    "behavior": [["PilotIT", "happy_path"]],
                },
            }
        )
    )
    return directory


@pytest.fixture
def report(consumer: Path) -> Path:
    path = consumer / "campsite-booking-service/target/surefire-reports/TEST-Example.xml"
    path.parent.mkdir(parents=True)
    path.write_text(
        '<testsuite tests="1" failures="0" errors="0" skipped="0">'
        '<testcase classname="ExampleTest" name="happy_path" /></testsuite>'
    )
    return path
