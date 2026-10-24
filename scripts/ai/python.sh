#!/usr/bin/env bash
# Consumer-owned checks; framework setup/lock remain the development dependency owner.
set -euo pipefail
root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)
cd "$root"
action=${1:-}
[[ $# == 1 ]] || { echo 'usage: python.sh format|format-check|lint|test-collect|test-fast|test-integration' >&2; exit 2; }
case "$action" in format|format-check|lint|test-collect|test-fast|test-integration) ;; *) echo 'unknown Python check' >&2; exit 2 ;; esac
: "${PYTHON:?select an absolute managed CPython 3.14.8 interpreter}"
[[ "$PYTHON" == /* ]] || { echo 'PYTHON must be absolute' >&2; exit 2; }
make -s -C ai/framework development-check PYTHON="$PYTHON"
export PYTHONDONTWRITEBYTECODE=1 RUFF_NO_CACHE=true
config=scripts/ai/pyproject.toml
files=(scripts/ai/test_reports.py scripts/ai/tests)
case "$action" in
  format) exec "$PYTHON" -m ruff format --config "$config" "${files[@]}" ;;
  format-check) exec "$PYTHON" -m ruff format --check --config "$config" "${files[@]}" ;;
  lint) exec "$PYTHON" -m ruff check --config "$config" "${files[@]}" ;;
  test-collect) cd scripts/ai; exec "$PYTHON" -m pytest --collect-only -q ;;
  test-fast) cd scripts/ai; exec "$PYTHON" -m pytest -q -m 'not integration and not acceptance' tests ;;
  test-integration) cd scripts/ai; exec "$PYTHON" -m pytest -q -m integration tests ;;
esac
