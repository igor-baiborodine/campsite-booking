<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Contributing](#contributing)
  - [Application and validation owners](#application-and-validation-owners)
  - [Explicit Java tool setup and selected-file checks](#explicit-java-tool-setup-and-selected-file-checks)
  - [Dependency data and findings](#dependency-data-and-findings)
  - [Shared framework preparation and checks](#shared-framework-preparation-and-checks)
  - [Explicit upgrade, rollback and review](#explicit-upgrade-rollback-and-review)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Contributing

## Application and validation owners

Keep the API and service modules, their POMs, YAML, database profiles and Flyway migrations
unchanged unless a task explicitly changes behavior. Java 17 and Maven 3.9.9 are the accepted
local validation tools. The Maven wrapper's historical 3.5.4 release is not an accepted substitute.
Run commands from this root; scripts also resolve it correctly when called from a nested directory.
The runner verifies both PATH Java and Maven's reported JVM are Java 17. A different `JAVA_HOME`
fails before Maven clean/build effects; align it with the intended JDK instead of relying on PATH.

Select an explicit Maven settings file through `AI_MAVEN_SETTINGS`. For credential-free local
validation a private file containing `<settings/>` is sufficient with accessible public dependencies.
Do not put credential-bearing settings in source or logs. Maven writes its cache and module `target/`
outputs. Docker must be available for owned Testcontainers MySQL integration; these are disposable
local tests, not shared/live database operations. Record the actual image digest and cleanup.

```sh
scripts/ai/java.sh unit
scripts/ai/java.sh integration
scripts/ai/java.sh behavior
scripts/ai/java.sh generate
scripts/ai/java.sh static
```

The baseline is 49 unit tests and 27 integration tests with no skips. Integration executes Failsafe
verify and the original JaCoCo 75% class-line gate with its original exclusions. Behavior temporarily
injects `scripts/ai/fixtures/FrameworkPilotIT.java` into the original test package, refuses an existing
destination, runs three isolated HTTP/MySQL cases plus original units, and removes only its unchanged
owned copy. It disables coverage only for this focused suite, without a full-coverage claim. If the
process is killed ungracefully, inspect and remove only the known unchanged fixture before retrying.
Generation retains OpenAPI Generator 6.5.0 and the original YAML/options; compare all 19 output files,
normalizing only known `@Generated` annotation timestamps. Never use replacement output as its own oracle.
Static analysis uses a separate tool POM, Maven PMD 3.26.0 / PMD 7.7.0 and three declared rules;
it is not whole-program or broad type-resolution validation.

Unit, integration and behavior also require explicit absolute `PYTHON` selecting managed CPython
3.14.8 (provisioning is described below). After Maven succeeds, `scripts/ai/test_reports.py` checks
fresh Surefire/Failsafe XML against `scripts/ai/expected-test-cases.json`: missing, unexpected,
duplicate, skipped, failed, malformed or stale results fail the gate. The manifest carries the
independent pre-adoption case identities and retained behavior-fixture provenance. Review it
alongside intentional test additions/renames/removals; never regenerate expected cases from a
failing run to obtain a pass. The named C-U19 integration failure still returns nonzero.

Fixed test gates reject nonempty `MAVEN_ARGS` and test-skip, failure-ignore or clean-skip properties
in JVM option variables and project `.mvn` configuration before invoking Maven. Ordinary JVM memory
options remain supported. Option values are not echoed. Report verification also catches skips,
failure-ignore behavior and discovery overrides introduced through other Maven configuration.

The report checker uses only the Python standard library. Its development commands reuse the
framework-owned runtime/locked pytest and Ruff environment, without installing from a check:

```sh
scripts/ai/python.sh format
scripts/ai/python.sh format-check
scripts/ai/python.sh lint
scripts/ai/python.sh test-collect
scripts/ai/python.sh test-fast
scripts/ai/python.sh test-integration
```

These commands own only `scripts/ai/test_reports.py` and `scripts/ai/tests/`; configuration is in
`scripts/ai/pyproject.toml`. Fast cases use temporary XML/JSON fixtures. Integration cases execute
the actual shell/report owners in disposable consumers with fake Java/Maven commands; they create
no database and do not replace actual `java.sh` unit/integration/behavior validation. Default test
discovery is confined to these tests and excludes full application acceptance. Only `format`
changes source; other development commands write owned test scratch at most.

## Explicit Java tool setup and selected-file checks

Choose a dedicated directory outside source as `AI_JAVA_TOOLS`, then explicitly run
`scripts/ai/tools_setup.sh`. It downloads Google Java Format 1.24.0 and Checkstyle 10.21.4 from
their official release endpoints with bounded requests, verifies `scripts/ai/tool-sha256.txt`
and preserves mismatched existing tools. Checks never call setup or download a replacement.

```sh
scripts/ai/java.sh format scripts/ai/fixtures/FrameworkPilotIT.java
scripts/ai/java.sh format-check scripts/ai/fixtures/FrameworkPilotIT.java
scripts/ai/java.sh imports scripts/ai/fixtures/FrameworkPilotIT.java
```

Pass only explicit task-owned handwritten paths. Formatting is the only mutating check;
format-check/imports preserve input bytes and reject generated/output or redirected paths.
Google formatting plus AvoidStarImport does not certify every style rule. Existing 15 format
and 16 wildcard-import findings in 11 files remain separate maintainer-owned cleanup under C-U14.
Do not reformat the original corpus or rename tests during adoption.

## Dependency data and findings

`AI_DEPENDENCY_DATA` must identify a complete, current Dependency-Check 13.0.0 database.
Preparation is an explicit separate operation, using a fresh dedicated directory and the official
Dependency-Check mirror configuration. An example credential-free preparation, with a 10-minute
budget, is:

```sh
timeout --kill-after=5s 600s mvn -B -s "$AI_MAVEN_SETTINGS" org.owasp:dependency-check-maven:13.0.0:update-only -DdataDirectory="$AI_DEPENDENCY_DATA" '-DnvdDatafeedUrl=https://dependency-check.github.io/DependencyCheck_Builder/nvd_cache/nvdcve-{0}.json.gz' -DossindexAnalyzerEnabled=false
scripts/ai/java.sh dependency
```

Inspect import diagnostics and last-checked/last-modified metadata before accepting the prepared
database. Partial imports, stale data and update errors are blockers even if a tool returns zero.
The scan runs with autoUpdate=false, failOnError=true and failBuildOnCVSS=7; reports/cache locks
are local effects. Existing 86 dependencies / 14 flagged dependencies / 89 advisories are a failed
baseline, assigned to the maintainer for separate cleanup, not exploitability conclusions or a
security waiver. Retain the original failing result and investigate new unexplained findings.
Never suppress findings or lower thresholds to make adoption green.

## Shared framework preparation and checks

The submodule gitlink is the exact pin; `.gitmodules` declares the approved source and
`refs/heads/main`. A fresh clone explicitly prepares the recorded revision:

```sh
git submodule update --init --checkout -- ai/framework
```

Guidance reads need no Python or Java runtime. Executable helpers require CPython 3.14.8, with
framework-owned `pyproject.toml`/`uv.lock` and uv 0.13.1. Provision explicitly with
`make -C ai/framework setup`, then export `PYTHON` to the absolute `ai/framework/.venv/bin/python`.
An already verified compatible managed interpreter may be explicitly selected instead.
Provision doctoc with `make -C ai/framework markdown-setup`; export `AI_DOCTOC_DIR` to the absolute
`ai/framework` directory. This installs only ignored development dependencies, not modified shared source.

```sh
scripts/ai/framework.sh integrity
scripts/ai/framework.sh compose
scripts/ai/framework.sh source
scripts/ai/framework.sh freshness
scripts/ai/markdown.sh format AGENTS.md CLAUDE.md CONTRIBUTING.md ai/consumer-profile.md
scripts/ai/markdown.sh check AGENTS.md CLAUDE.md CONTRIBUTING.md ai/consumer-profile.md
```

Integrity/composition are offline-capable after explicit preparation. Composition checks the Java
API/test and auxiliary Python test routes in separate selections, their command bindings, and both
installed clients, including exact adapter bytes. Freshness independently
queries the configured upstream for at most 20 seconds and requires pin/HEAD/upstream equality;
it never fetches, repairs or advances the pin. The index pin is checked while an adoption is staged;
pass `--commit <consumer-sha>` for an explicit committed-state check where supported.

The Markdown owner uses doctoc 2.5.0 on named files; checks use temporary copies. Exactly the six
canonical adapters in each client directory are exempt from TOC transformation; unrelated or
lookalike skills retain the ordinary check. Exact-template validation still rejects adapter drift.

The [AI consumer workflow](.github/workflows/ai-framework.yml) prepares the recorded pin, sets up
managed Python and Markdown tools, validates shared source/integrity/composition, checks tracked
consumer AI Markdown, and tests local validation tooling. It uses the `AI Consumer Validation`
name, `ai-check` job, Ubuntu 24.04, a 20-minute
timeout, path-filtered PRs and manual dispatch. PR runs check the PR head; manual runs check the
selected revision. Campsite Booking's command bindings are `make -C ai/framework setup`,
`scripts/ai/framework.sh`, `scripts/ai/markdown.sh check` and `scripts/ai/python.sh`.
Online freshness remains separate. The existing Java application commands and setup are preserved in
[Java adoption validation](.github/workflows/java-adoption.yml); their redesign is separate work.

Before writing local evidence, manually add `/ai/artifacts/` to `.git/info/exclude`. The root
agent guide requires ignored content and empty tracked/staged artifact listings; tools never
repair this setup. Shared snapshots require output inside the source consumer's ignored artifact
root. This extraction ticket retains its external artifact home and explicit manual snapshot.

## Explicit upgrade, rollback and review

Inspect existing work before changing a dependency. Choose and review a specific upstream commit;
fetch it explicitly, check it out detached in `ai/framework`, regenerate adapters from that revision's
`adapters/{codex,claude}/skills/*/SKILL.md.in` using the local framework path, and reconcile the local
profile. Stage the gitlink only as part of this deliberate operation; never use `update --remote`
as a check. Run integrity, both-client composition, relevant behavioral checks and fresh upstream
equality. An ahead, stale or diverged pin fails freshness even when reproducible offline.

Rollback deliberately restores the prior compatible gitlink/profile/adapter combination after
reviewing local edits. Preserve unrelated source and all historical ignored evidence. Run offline
integrity/composition and report upstream inequality honestly; rollback does not force upstream
freshness to pass. Do not reset or clean the checkout to simplify recovery.

Mandatory local review precedes acceptance. Agent-policy changes additionally require independent
human maintainer acceptance of the concrete diff. Commit, push and publication remain separate
explicit permissions. CI wiring does not claim a hosted pass until an actual run is recorded;
actual Codex/Claude reads and actions are separate from structural checks.
