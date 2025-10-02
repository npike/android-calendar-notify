# Tasks: Initial Event Setup and Notification

**Feature Branch**: `002-this-project-already`

## Phase 2: Task Planning

This document outlines the actionable tasks for implementing the "Initial Event Setup and Notification" feature. Tasks are ordered by dependencies, prioritizing setup, testing (TDD), data layer, domain layer, and then UI/service layer.

### Setup Tasks

- [x] **T001**: Modify `app/src/main/java/net/npike/android/calendarnotify/data/local/EventEntity.kt` to add a new `isSeen: Boolean` field with a default value of `false`.
- [x] **T002**: Update `app/src/main/java/net/npike/android/calendarnotify/data/local/EventDao.kt` to include methods for updating the `isSeen` status of events.
- [x] **T003**: Create a new file `app/src/main/java/net/npike/android/calendarnotify/data/local/SetupStatusManager.kt` to manage the `isInitialSetupComplete` flag using `Preferences DataStore`.

### Test Tasks

- [x] **T004 [P]**: Create `app/src/test/java/net/npike/android/calendarnotify/data/local/SetupStatusManagerTest.kt` and write unit tests for `SetupStatusManager` to verify persistent storage and retrieval of `isInitialSetupComplete`.
- [x] **T005 [P]**: Enhance `app/src/test/java/net/npike/android/calendarnotify/data/repository/CalendarRepositoryTest.kt` to include unit tests for event fetching within a specific date range and updating the `isSeen` status.
- [x] **T006 [P]**: Create `app/src/test/java/net/npike/android/calendarnotify/service/EventWorkerTest.kt` and write unit tests for `EventWorker`, covering foreground service notification display, initial event fetching, and marking events as seen.
- [x] **T007 [P]**: Create `app/src/test/java/net/npike/android/calendarnotify/ui/screen/InitialSetupScreenViewModelTest.kt` and write unit tests for `InitialSetupScreenViewModel` to verify UI state management and triggering of the setup process.

### Core Tasks

- [x] **T008**: Implement `app/src/main/java/net/npike/android/calendarnotify/data/local/SetupStatusManager.kt` using `Preferences DataStore` to store and retrieve the `isInitialSetupComplete` flag.
- [x] **T009**: Modify `app/src/main/java/net/npike/android/calendarnotify/data/repository/CalendarRepository.kt` to include methods for fetching calendar events within a specified date range (now to one year from now) and updating the `isSeen` status of events.
- [x] **T010**: Create `app/src/main/java/net/npike/android/calendarnotify/ui/screen/InitialSetupScreenViewModel.kt` to manage the UI state of the initial setup screen, trigger the setup process, and handle permission revocation.
- [x] **T011**: Implement `app/src/main/java/net/npike/android/calendarnotify/service/EventWorker.kt` to perform the initial event fetching and marking, and to display a non-dismissible foreground service notification during its execution.
- [x] **T012**: Create `app/src/main/java/net/npike/android/calendarnotify/ui/screen/InitialSetupScreen.kt` as a Composable to display the setup progress, including a loading indicator and relevant messages.

### Integration Tasks

- [x] **T013**: Integrate `SetupStatusManager` into `app/src/main/java/net/npike/android/calendarnotify/di/AppModule.kt` or a new `DataStoreModule.kt` for dependency injection.
- [x] **T014**: Integrate `InitialSetupScreen` into the navigation flow of `app/src/main/java/net/npike/android/calendarnotify/MainActivity.kt`, ensuring it's shown on first launch after permissions are granted.
- [x] **T015**: Modify `InitialSetupScreenViewModel` or `MainActivity` to trigger the `EventWorker` after calendar permissions are granted and the initial setup is determined to be incomplete.

### Polish Tasks

- [x] **T016 [P]**: Ensure robust error handling for permission revocation in `EventWorker` and `InitialSetupScreenViewModel`, prompting the user to re-grant permissions as per the spec.
- [x] **T017 [P]**: Verify that storage issues during event marking are gracefully ignored in `EventWorker` as per the spec.
- [x] **T018 [P]**: Update `GEMINI.md` with any new dependencies or technologies introduced by this feature (e.g., DataStore, WorkManager foreground service specifics).

## Parallel Execution Guidance

Tasks marked with `[P]` can be executed in parallel as they are largely independent. For example, `T004`, `T005`, `T006`, and `T007` (test tasks) can be worked on concurrently. Similarly, `T016`, `T017`, and `T018` (polish tasks) can be parallelized.

## Example Task Agent Commands

To execute a task, you would typically use a command like:

```bash
/implement T001
```

Or for a parallel task:

```bash
/implement T004
```
