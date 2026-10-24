#!/usr/bin/env bash
set -euo pipefail
consumer_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)
cd "$consumer_root"
action=${1:-}
[[ $# -gt 0 ]] && shift
case "$action" in unit|integration|behavior|generate|static|dependency|format|format-check|imports) ;; *) echo 'usage: java.sh unit|integration|behavior|generate|static|dependency|format|format-check|imports [handwritten-files]' >&2; exit 2 ;; esac
java_version=$(java -version 2>&1)
[[ "$java_version" == *'version "17.'* ]] || { echo 'Java 17 required' >&2; exit 1; }
case "$action" in
  format|format-check|imports)
    : "${AI_JAVA_TOOLS:?explicit pinned tool directory required}"
    (cd "$AI_JAVA_TOOLS" && sha256sum --status -c "$consumer_root/scripts/ai/tool-sha256.txt") || { echo 'missing or mismatched pinned Java tools; run explicit tools setup' >&2; exit 1; }
    [[ $# -gt 0 ]] || { echo 'explicit handwritten Java files required' >&2; exit 2; }
    for file in "$@"; do
      case "$file" in campsite-booking-service/src/main/java/*.java|campsite-booking-service/src/test/java/*.java|scripts/ai/fixtures/*.java) ;; *) echo 'outside selected handwritten roots' >&2; exit 2 ;; esac
      [[ -f "$file" && ! -L "$file" && "$file" != *'..'* && $(realpath -e -- "$file") == "$consumer_root/$file" ]] || { echo 'invalid or redirected file selection' >&2; exit 2; }
    done
    case "$action" in
      format) exec java -jar "$AI_JAVA_TOOLS/google-java-format-1.24.0-all-deps.jar" --replace "$@" ;;
      format-check) exec java -jar "$AI_JAVA_TOOLS/google-java-format-1.24.0-all-deps.jar" --dry-run --set-exit-if-changed "$@" ;;
      imports) exec java -jar "$AI_JAVA_TOOLS/checkstyle-10.21.4-all.jar" -c scripts/ai/imports.xml "$@" ;;
    esac ;;
  *)
    [[ $# == 0 ]] || { echo 'Maven gate arguments are fixed; select the named gate' >&2; exit 2; }
    : "${AI_MAVEN_SETTINGS:?explicit Maven settings file required}"
    [[ -f "$AI_MAVEN_SETTINGS" ]] || { echo 'Maven settings file missing' >&2; exit 2; }
    case "$action" in
      unit|integration|behavior)
        : "${PYTHON:?select an absolute managed CPython 3.14.8 interpreter for test report checks}"
        [[ "$PYTHON" == /* ]] || { echo 'PYTHON must be absolute' >&2; exit 2; }
        ai/framework/scripts/runtime_check.sh "$PYTHON"
        started_ns=$("$PYTHON" -B scripts/ai/test_reports.py preflight) ;;
    esac
    maven_version=$(mvn -version)
    [[ "$maven_version" == *'Apache Maven 3.9.9 '* ]] || { echo 'Maven 3.9.9 required; wrapper is not an accepted substitute' >&2; exit 1; }
    [[ "$maven_version" =~ (^|$'\n')Java\ version:\ 17[.,] ]] || { echo 'Maven must run on Java 17; check JAVA_HOME and PATH' >&2; exit 1; }
    mvn_cmd=(mvn -B -s "$AI_MAVEN_SETTINGS")
    case "$action" in
      unit)
        "${mvn_cmd[@]}" clean test
        "$PYTHON" -B scripts/ai/test_reports.py verify unit "$started_ns" ;;
      integration)
        "${mvn_cmd[@]}" clean verify '-Dit.test=*IT,!FrameworkPilotIT'
        "$PYTHON" -B scripts/ai/test_reports.py verify integration "$started_ns" ;;
      behavior)
        target=campsite-booking-service/src/test/java/com/kiroule/campsitebooking/FrameworkPilotIT.java
        [[ ! -e "$target" && ! -L "$target" ]] || { echo 'behavior fixture destination already exists; preserve it and stop' >&2; exit 1; }
        # No application POM change: inject only the retained fixture for this isolated run.
        (set -o noclobber; cat scripts/ai/fixtures/FrameworkPilotIT.java > "$target")
        fixture_sha=$(sha256sum "$target")
        cleanup() {
          if [[ -f "$target" && $(sha256sum "$target") == "$fixture_sha" ]]; then rm -- "$target";
          else echo 'fixture changed during run; retained for inspection' >&2; fi
        }
        trap cleanup EXIT
        trap 'exit 130' INT
        trap 'exit 143' TERM
        "${mvn_cmd[@]}" clean verify -Dit.test=FrameworkPilotIT -Djacoco.skip=true
        "$PYTHON" -B scripts/ai/test_reports.py verify behavior "$started_ns" ;;
      generate) exec "${mvn_cmd[@]}" -pl campsite-booking-api -am clean generate-sources ;;
      static) exec "${mvn_cmd[@]}" -f scripts/ai/static-pom.xml org.apache.maven.plugins:maven-pmd-plugin:3.26.0:check ;;
      dependency)
        : "${AI_DEPENDENCY_DATA:?explicit complete current vulnerability database required}"
        [[ -s "$AI_DEPENDENCY_DATA/odc.mv.db" ]] || { echo 'prepared Dependency-Check database missing; setup is separate' >&2; exit 1; }
        exec "${mvn_cmd[@]}" org.owasp:dependency-check-maven:13.0.0:aggregate -DautoUpdate=false -DfailOnError=true -DfailBuildOnCVSS=7 -DdataDirectory="$AI_DEPENDENCY_DATA" -DossindexAnalyzerEnabled=false -Dformats=JSON,HTML ;;
    esac ;;
esac
