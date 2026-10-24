#!/usr/bin/env bash
set -euo pipefail
consumer_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)
cd "$consumer_root"
mode=${1:-}
[[ "$mode" == format || "$mode" == check ]] || { echo 'usage: markdown.sh format|check file.md ...' >&2; exit 2; }
shift
[[ $# -gt 0 ]] || { echo 'explicit Markdown files required' >&2; exit 2; }
: "${AI_DOCTOC_DIR:?select an explicitly provisioned package prefix containing doctoc 2.5.0}"
doctor="$AI_DOCTOC_DIR/node_modules/doctoc"
[[ $(node -p 'require(process.argv[1]).version' "$doctor/package.json") == 2.5.0 ]] || { echo 'doctoc 2.5.0 required' >&2; exit 1; }
files=()
for file in "$@"; do
  target=$(realpath -e -- "$file")
  [[ "$target" == "$consumer_root/"* && "$target" == *.md && ! -L "$file" && -f "$target" ]] || { echo 'invalid consumer Markdown path' >&2; exit 2; }
  relative=${target#"$consumer_root/"}
  [[ "$relative" != ai/framework/* ]] || { echo 'shared dependency is immutable' >&2; exit 2; }
  case "$relative" in
    .agents/skills/*/SKILL.md|.claude/skills/*/SKILL.md)
      name=${relative#*/skills/}; name=${name%/SKILL.md}
      case "$name" in code-review|pr-description|ticket-requirements-workflow|ticket-specification-workflow|ticket-implementation-workflow|ticket-post-step-workflow) continue ;; esac ;;
  esac
  files+=("$target")
done
scratch=$(mktemp -d)
trap 'rm -rf -- "$scratch"' EXIT
for target in "${files[@]}"; do
  if [[ $mode == format ]]; then
    node "$doctor/doctoc.js" --github --notitle --mintocitems 1 "$target" >/dev/null
  else
    cp -- "$target" "$scratch/input.md"
    node "$doctor/doctoc.js" --github --notitle --mintocitems 1 "$scratch/input.md" >/dev/null
    cmp -s -- "$target" "$scratch/input.md" || { echo "stale TOC: ${target#"$consumer_root/"}" >&2; exit 1; }
  fi
done
