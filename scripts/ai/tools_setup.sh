#!/usr/bin/env bash
# Explicit setup only. Checks never call this script.
set -euo pipefail
root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)
: "${AI_JAVA_TOOLS:?choose a dedicated directory outside the source tree}"
mkdir -p -- "$AI_JAVA_TOOLS"
staging=$(mktemp -d "$AI_JAVA_TOOLS/.setup.XXXXXX")
trap 'rm -rf -- "$staging"' EXIT
curl --fail --location --connect-timeout 10 --max-time 120 --output "$staging/google-java-format-1.24.0-all-deps.jar" https://github.com/google/google-java-format/releases/download/v1.24.0/google-java-format-1.24.0-all-deps.jar
curl --fail --location --connect-timeout 10 --max-time 120 --output "$staging/checkstyle-10.21.4-all.jar" https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.21.4/checkstyle-10.21.4-all.jar
(cd "$staging" && sha256sum -c "$root/scripts/ai/tool-sha256.txt")
for file in google-java-format-1.24.0-all-deps.jar checkstyle-10.21.4-all.jar; do
  if [[ -e "$AI_JAVA_TOOLS/$file" ]]; then
    cmp -s "$staging/$file" "$AI_JAVA_TOOLS/$file" || { echo 'existing tool differs; preserve it and select a clean directory' >&2; exit 1; }
  else mv -- "$staging/$file" "$AI_JAVA_TOOLS/$file"; fi
done
