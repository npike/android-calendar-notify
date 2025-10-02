# Implementation Plan: Initial Event Setup and Notification

**Branch**: `002-this-project-already` | **Date**: September 30, 2025 | **Spec**: `/specs/002-this-project-already/spec.md`
**Input**: Feature specification from `/specs/002-this-project-already/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
   → If not found: ERROR "No feature spec at {path}"
2. Fill Technical Context (scan for NEEDS CLARIFICATION)
   → Detect Project Type from file system structure or context (web=frontend+backend, mobile=app+api)
   → Set Structure Decision based on project type
3. Fill the Constitution Check section based on the content of the constitution document.
4. Evaluate Constitution Check section below
   → If violations exist: Document in Complexity Tracking
   → If no justification possible: ERROR "Simplify approach first"
   → Update Progress Tracking: Initial Constitution Check
5. Execute Phase 0 → research.md
   → If NEEDS CLARIFICATION remain: ERROR "Resolve unknowns"
6. Execute Phase 1 → contracts, data-model.md, quickstart.md, agent-specific template file (e.g., `CLAUDE.md` for Claude Code, `.github/copilot-instructions.md` for GitHub Copilot, `GEMINI.md` for Gemini CLI, `QWEN.md` for Qwen Code or `AGENTS.md` for opencode).
7. Re-evaluate Constitution Check section
   → If new violations: Refactor design, return to Phase 1
   → Update Progress Tracking: Post-Design Constitution Check
8. Plan Phase 2 → Describe task generation approach (DO NOT create tasks.md)
9. STOP - Ready for /tasks command
```

**IMPORTANT**: The /plan command STOPS at step 7. Phases 2-4 are executed by other commands:
- Phase 2: /tasks command creates tasks.md
- Phase 3-4: Implementation execution (manual or via tools)

## Summary
The app will perform an initial setup on first launch after calendar permissions are granted. This setup involves fetching all calendar events for the next year and marking them internally as "seen". A setup screen will be displayed during this process, and a non-dismissible notification will be shown if the app is in the background. The app will stop setup and prompt the user to re-grant permissions if calendar permissions are revoked during setup. Storage issues during event marking will be ignored.

## Technical Context
**Language/Version**: Kotlin (existing project)  
**Primary Dependencies**: Android Calendar API, WorkManager, Room (for local data storage), Hilt (for dependency injection), Compose (for UI)  
**Storage**: Room Database  
**Testing**: JUnit, Mockito, Turbine (for Flow testing)  
**Target Platform**: Android
**Project Type**: Mobile  
**Performance Goals**: Initial setup should complete in a reasonable time, notification should be responsive.  
**Constraints**: Offline-capable (initial setup can run in background), non-dismissible notification during background setup.  
**Scale/Scope**: Handling events for one year from now.

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] All Flow-based asynchronous operations in unit tests MUST be tested using the Turbine library. (This is a constitutional requirement and will be adhered to during implementation and testing.)
- [x] Kotlin First: All new code will be in Kotlin.
- [x] Material Design: UI will follow Material Design principles.
- [x] Unit Tests (NON-NEGOTIABLE): All new logic will have unit tests.
- [x] Offline First: The background setup supports offline operation.
- [x] Clean Architecture: Adhere to existing clean architecture.
- [x] Architecture: Use Flow objects, StateFlow/Compose State.
- [x] UI State Hoisting: Composables will use state hoisting.

## Project Structure

### Documentation (this feature)
```
specs/002-this-project-already/
├── plan.md              # This file (/plan command output)
├── research.md          # Phase 0 output (/plan command)
├── data-model.md        # Phase 1 output (/plan command)
├── quickstart.md        # Phase 1 output (/plan command)
├── contracts/           # Phase 1 output (/plan command)
└── tasks.md             # Phase 2 output (/tasks command - NOT created by /plan)
```

### Source Code (repository root)
```
app/
└── src/
    └── main/
        └── java/
            └── net/
                └── npike/
                    └── android/
                        └── calendarnotify/
                            ├── data/
                            │   ├── local/
                            │   │   ├── AppDatabase.kt
                            │   │   ├── CalendarDao.kt
                            │   │   ├── CalendarEntity.kt
                            │   │   ├── DataStoreManager.kt
                            │   │   ├── EventDao.kt
                            │   │   └── EventEntity.kt
                            │   └── repository/
                            │       └── CalendarRepository.kt
                            ├── di/
                            │   ├── AppModule.kt
                            │   ├── DatabaseModule.kt
                            │   ├── DataStoreModule.kt
                            │   └── WorkManagerModule.kt
                            ├── domain/
                            │   ├── model/
                            │   │   ├── Calendar.kt
                            │   │   └── Event.kt
                            │   └── usecase/
                            │       ├── CheckForNewEventsUseCase.kt
                            │       ├── GetCalendarsUseCase.kt
                            │       └── UpdateCalendarUseCase.kt
                            ├── service/
                            │   ├── EventWorker.kt
                            │   ├── NotificationHelper.kt
                            │   └── WorkManagerInitializer.kt
                            ├── ui/
                            │   ├── screen/
                            │   │   ├── CalendarScreen.kt
                            │   │   ├── CalendarScreenViewModel.kt
                            │   │   └── PermissionRationaleScreen.kt
                            │   └── theme/
                            │       ├── Color.kt
                            │       ├── Shapes.kt
                            │       ├── Theme.kt
                            │       └── Type.kt
                            └── viewmodel/
```

**Structure Decision**: Mobile application, adhering to existing project structure. New components will be added to appropriate existing directories (e.g., new use cases in `domain/usecase`, new screens in `ui/screen`, new data entities in `data/local`).

## Phase 0: Outline & Research
1. **Extract unknowns from Technical Context** above:
   - How to efficiently query calendar events for a year range.
   - How to mark events as "seen" internally (new field in `EventEntity` or separate table).
   - How to manage the "setup complete" status persistently.
   - How to create a non-dismissible notification for background work.
   - How to display a setup screen and manage its state.

2. **Generate and dispatch research agents**:
   - Task: "Research efficient Android Calendar Provider queries for a date range."
   - Task: "Research best practices for persistent feature flags/status in Android (e.g., DataStore, Room)."
   - Task: "Research creating non-dismissible foreground service notifications in Android WorkManager."
   - Task: "Research Android Compose UI patterns for a loading/setup screen."

3. **Consolidate findings** in `research.md` using format:
   - Decision: [what was chosen]
   - Rationale: [why chosen]
   - Alternatives considered: [what else evaluated]

**Output**: research.md with all NEEDS CLARIFICATION resolved

## Phase 1: Design & Contracts
*Prerequisites: research.md complete*

1. **Extract entities from feature spec** → `data-model.md`:
   - **Event**: Existing entity, needs a new field `isSeen: Boolean` (default `false`).
   - **SetupStatus**: New entity/preference to store `isInitialSetupComplete: Boolean` (default `false`).

2. **Generate API contracts** from functional requirements:
   - No external API contracts needed. Internal contracts will be defined by new use cases and repository methods.

3. **Generate contract tests** from contracts:
   - Not applicable for external APIs. Internal contract tests will be part of unit tests for use cases and repositories.

4. **Extract test scenarios** from user stories:
   - Test initial setup screen display.
   - Test background notification display.
   - Test event fetching and marking for a year range.
   - Test that setup does not re-run after completion.
   - Test handling of permission revocation.
   - Test handling of storage issues (ignoring them).

5. **Update agent file incrementally** (O(1) operation):
   - Run `.specify/scripts/bash/update-agent-context.sh gemini`
     **IMPORTANT**: Execute it exactly as specified above. Do not add or remove any arguments.
   - If exists: Add only NEW tech from current plan
   - Preserve manual additions between markers
   - Update recent changes (keep last 3)
   - Keep under 150 lines for token efficiency
   - Output to repository root

**Output**: data-model.md, /contracts/*, failing tests, quickstart.md, agent-specific file

## Phase 2: Task Planning Approach
*This section describes what the /tasks command will do - DO NOT execute during /plan*

**Task Generation Strategy**:
- Load `.specify/templates/tasks-template.md` as base
- Generate tasks from Phase 1 design docs (data model, quickstart, research findings)
- Each new entity/field → model/database update task
- Each use case → use case implementation task
- Each UI component → UI implementation task
- Each notification requirement → notification implementation task
- Each test scenario → unit/integration test task

**Ordering Strategy**:
- TDD order: Tests before implementation 
- Dependency order: Data layer -> Domain layer -> UI/Service layer
- Mark [P] for parallel execution (independent files)

**Estimated Output**: 25-30 numbered, ordered tasks in tasks.md

## Phase 3+: Future Implementation
*These phases are beyond the scope of the /plan command*

**Phase 3**: Task execution (/tasks command creates tasks.md)  
**Phase 4**: Implementation (execute tasks.md following constitutional principles)  
**Phase 5**: Validation (run tests, execute quickstart.md, performance validation)

## Complexity Tracking
*Fill ONLY if Constitution Check has violations that must be justified*

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
|           |            |                                     |


## Progress Tracking
*This checklist is updated during execution flow*

**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [x] Phase 2: Task planning complete (/plan command - describe approach only)
- [x] Phase 3: Tasks generated (/tasks command)
- [x] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved
- [ ] Complexity deviations documented

---
*Based on Constitution v2.1.1 - See `/memory/constitution.md`*