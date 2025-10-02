# Tasks: New Calendar Event Notifier

**Input**: Design documents from `/specs/001-build-an-android/`

## Phase 3.1: Setup
- [X] T001 Add dependencies for Room, Hilt, WorkManager, and Compose to `app/build.gradle.kts`
- [X] T002 Configure Hilt in the `Application` class.

## Phase 3.2: Core Data Layer
- [X] T003 [P] Create `Event` entity in `app/src/main/java/net/npike/android/calendarnotify/data/local/EventEntity.kt`
- [X] T004 [P] Create `Calendar` entity in `app/src/main/java/net/npike/android/calendarnotify/data/local/CalendarEntity.kt`
- [X] T005 Create `EventDao` interface in `app/src/main/java/net/npike/android/calendarnotify/data/local/EventDao.kt`
- [X] T006 Create `CalendarDao` interface in `app/src/main/java/net/npike/android/calendarnotify/data/local/CalendarDao.kt`
- [X] T007 Create `AppDatabase` class in `app/src/main/java/net/npike/android/calendarnotify/data/local/AppDatabase.kt`
- [X] T008 Create a Hilt module to provide the database and DAOs.
- [X] T009 Create `CalendarRepository` in `app/src/main/java/net/npike/android/calendarnotify/data/repository/CalendarRepository.kt` to interact with the `CalendarProvider` and the local database.

## Phase 3.3: Core Domain Layer
- [X] T010 [P] Create `GetCalendarsUseCase` in `app/src/main/java/net/npike/android/calendarnotify/domain/usecase/GetCalendarsUseCase.kt`
- [X] T011 [P] Create `UpdateCalendarUseCase` in `app/src/main/java/net/npike/android/calendarnotify/domain/usecase/UpdateCalendarUseCase.kt`
- [X] T012 [P] Create `CheckForNewEventsUseCase` in `app/src/main/java/net/npike/android/calendarnotify/domain/usecase/CheckForNewEventsUseCase.kt`

## Phase 3.4: Worker and Notifications
- [X] T013 Create `EventWorker` in `app/src/main/java/net/npike/android/calendarnotify/service/EventWorker.kt`
- [X] T014 Enqueue the `EventWorker` with a `ContentUriTrigger` and a `PeriodicWorkRequest`.
- [X] T015 Create a notification channel for new event notifications.
- [X] T016 Create a helper class to build and display notifications.

## Phase 3.5: UI Layer
- [X] T017 Create `CalendarScreenViewModel` in `app/src/main/java/net/npike/android/calendarnotify/ui/screen/CalendarScreenViewModel.kt`
- [X] T018 Create `CalendarScreen` Composable in `app/src/main/java/net/npike/android/calendarnotify/ui/screen/CalendarScreen.kt` to display the list of calendars.
- [X] T019 Implement the UI to allow users to toggle calendar monitoring.

## Phase 3.6: Testing
- [X] T020 [P] Write unit tests for `CalendarRepository`.
- [X] T021 [P] Write unit tests for the UseCases.
- [X] T022 [P] Write unit tests for `CalendarScreenViewModel`.
- [X] T023 Write integration tests for the database.

## Dependencies
- Data layer tasks (T003-T009) should be completed before domain layer tasks (T010-T012).
- Domain layer tasks should be completed before UI and worker tasks.
