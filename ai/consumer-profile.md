<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Campsite Booking Consumer Profile](#campsite-booking-consumer-profile)
  - [Composition and local ownership](#composition-and-local-ownership)
  - [Java conventions and behavioral references](#java-conventions-and-behavioral-references)
  - [Commands and evidence](#commands-and-evidence)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Campsite Booking Consumer Profile

## Composition and local ownership

The consumer root [AGENTS.md](../AGENTS.md) adopts the [shared contract](framework/consumer.md).
[profile.json](profile.json) selects core, Java, auxiliary Python and house components, Google Java Style, and local
command bindings. `.gitmodules` owns source and upstream main; the index gitlink owns the exact
working revision. Do not add a second SHA lock. The dependency is the same unmodified revision
used by Insurance Hub. Java selection is independent of that consumer's Go or legacy Java choices.

Local POMs own Java 17, Spring Boot 3.1.5, OpenAPI Generator 6.5.0, Surefire/Failsafe 3.2.1,
JaCoCo 0.8.10 and application dependencies. The accepted runner is Maven 3.9.9; the historical
3.5.4 wrapper is not a verified substitute. Keep API generation options, database profiles,
Flyway schema and booking rules local. Tool-only PMD configuration adds no application dependency.

## Java conventions and behavioral references

Apply shared Java implementation rules and the architecture/API/test routes when affected.
Adopt existing nested JUnit method groups, underscore scenario names, Given/When/Then,
`classUnderTest` and `result`, AssertJ/Mockito and explicit contract-relevant fixture fields.
MapStruct/Lombok, Spring constructor composition, transaction/retry proxies and Maven are selected
stack choices. Preserve module direction: generated contracts are implemented by service adapters;
DTOs, domain models and persistence entities remain distinct. Do not log request payloads or secrets.

Read [shared Java provenance](framework/provenance/java.md) for exact source anchors and B01–B06.
The API YAML, controller, exception advice and original tests own expected results. Preserve
create/read/availability behavior, validation 400, missing UUID 404, stale version 409 and overlapping
booking invariants. The YAML's response discrepancies and malformed GET UUID evidence gap remain
visible; this adoption does not repair them. Collect concurrent worker failures and assert real
stored rows. Original tests do not alone prove every shared resource/concurrency safeguard.

Google Java Format 1.24.0 and Checkstyle 10.21.4 AvoidStarImport apply to selected handwritten files;
generated `target/` output is excluded. Existing 15 formatting deviations and 16 wildcard imports
in 11 files remain separate cleanup owned by the campsite maintainer. The prior dependency scan
reported 86 dependencies, 14 flagged dependencies and 89 unique advisories. These remain failed
baseline findings, not risk acceptance or a passing scan; new unexplained findings require disposition.

## Commands and evidence

[CONTRIBUTING.md](../CONTRIBUTING.md) owns prerequisites, explicit setup, check effects and
upgrade/rollback instructions. All wrappers resolve this root from their own path, including nested
invocations. Framework helpers require explicit absolute CPython 3.14.8; no system fallback or
Insurance Hub application toolchain is required. Guidance loading itself needs no runtime.

Unit/integration/generation preserve the original reactor. Behavior injects only the retained
three-case fixture for an isolated run and removes its unchanged owned copy on exit. It disables
JaCoCo only for that focused run, never for full integration. Tests own disposable MySQL/Ryuk
resources; record their actual identity and verify teardown. All setup remains separate from checks.

Unit/integration/behavior additionally use the standard-library report checker and the independent
case manifest under `scripts/ai/`. They require explicit managed CPython 3.14.8 and reject inherited
skip/ignore settings plus missing, stale, skipped, failed or changed test discovery. Maven's own JVM
must report Java 17 before any build. The original application POMs and coverage thresholds remain
authoritative. Review manifest changes with intentional test changes; no automatic baseline refresh.

Python is limited to auxiliary validation. Load its selected rules only for that boundary; Java
application work does not acquire unrelated Python guidance. `scripts/ai/python.sh` owns scoped
format/lint/collection/fast/disposable checks using framework-owned CPython/dev pins and lock,
with `scripts/ai/pyproject.toml` owning consumer Ruff/pytest selection. The dependency remains immutable.

Integrity is offline-capable and checks the index pin; freshness queries the configured exact
upstream with a 20-second bound and never updates pins, refs or source. Composition explicitly
checks both installed clients. Local artifact preflight and GitHub issue synchronization follow
the root guide. Client support claims require observed canonical reads/actions per host/version;
correct adapters alone do not prove them. Other clients/cloud surfaces remain outside this pilot.
