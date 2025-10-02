<!--
Sync Impact Report
- Version change: 1.1.2 → 1.2.0
- List of modified principles: None
- Added sections:
  - VIII. Flow Testing with Turbine
- Removed sections: None
- Templates requiring updates:
  - .specify/templates/plan-template.md ⚠ pending
  - .specify/templates/spec-template.md ⚠ pending
  - .specify/templates/tasks-template.md ⚠ pending
  - .specify/templates/commands/*.md ⚠ pending
- Follow-up TODOs: None
-->
# android-calendar-notify Constitution

## Core Principles

### I. Kotlin First
All new code MUST be written in Kotlin. Java is only permitted for maintaining existing legacy code.

### II. Material Design
The user interface MUST adhere to Google's Material Design guidelines to ensure a consistent and intuitive user experience.

### III. Unit Tests (NON-NEGOTIABLE)
All business logic, view models, and data layers MUST be accompanied by unit tests. TDD is strongly encouraged.

### IV. Offline First
The application MUST be designed to be functional without a network connection wherever possible. Data synchronization should handle offline periods gracefully.

### V. Clean Architecture
The codebase MUST follow the principles of Clean Architecture, separating concerns into distinct layers (e.g., UI, Domain, Data).

### VI. Architecture
The Data Layer must expose Flow objects to the Domain/Presentation Layer. ViewModels must consume Flow and expose StateFlow or Compose State. Direct use of Callbacks or RxJava is forbidden.

### VII. UI State Hoisting
Composables MUST use state hoisting. ViewModel instances MUST NOT be passed directly into Composables. `hiltViewModel()` MUST NOT be used directly within Composables.

### VIII. Flow Testing with Turbine
All Flow-based asynchronous operations in unit tests MUST be tested using the Turbine library to ensure reliable and concise testing of flow emissions.

## Governance

All code reviews MUST verify compliance with this constitution. Any deviation requires explicit justification and approval.

**Version**: 1.2.0 | **Ratified**: 2025-09-29 | **Last Amended**: 2025-09-30