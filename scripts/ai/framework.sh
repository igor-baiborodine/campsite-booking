#!/usr/bin/env bash
set -euo pipefail
consumer_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)
cd "$consumer_root"
action=${1:-}
[[ $# -gt 0 ]] && shift
case "$action" in integrity|compose|freshness|source) ;; *) echo 'usage: framework.sh integrity|compose|freshness|source [args]' >&2; exit 2 ;; esac
: "${PYTHON:?select an absolute verified framework CPython 3.14.8 interpreter}"
[[ "$PYTHON" == /* ]] || { echo 'PYTHON must be absolute' >&2; exit 2; }
framework="$consumer_root/ai/framework"
[[ -f "$framework/tools/framework_check.py" ]] || { echo 'framework missing: explicitly initialize the recorded submodule pin' >&2; exit 1; }
"$framework/scripts/runtime_check.sh" "$PYTHON"
args=("$action" --framework-root "$framework")
if [[ "$action" != source ]]; then args+=(--consumer-root "$consumer_root" --profile ai/profile.json); fi
if [[ "$action" == compose ]]; then
  args+=(--language java --boundary api --boundary test --client codex --client claude --require unit --require integration --require behavior --require generate --require format-check --require imports --require static --require dependency --require markdown-format --require markdown-check --require snapshot --require artifact-preflight)
fi
if [[ "$action" == freshness ]]; then args+=(--timeout 20); fi
exec "$PYTHON" -B "$framework/tools/framework_check.py" "${args[@]}" "$@"
