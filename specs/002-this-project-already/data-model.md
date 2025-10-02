# Data Model: Initial Event Setup and Notification

## Entities

### Event
**Description**: Represents a calendar event. This is an existing entity that will be augmented.
**Attributes**:
- `id`: Long (Primary Key, existing)
- `title`: String (existing)
- `begin`: Long (start time in milliseconds, existing)
- `end`: Long (end time in milliseconds, existing)
- `allDay`: Boolean (existing)
- `eventLocation`: String? (existing)
- `isSeen`: Boolean (NEW - default `false`. Indicates if the event has been processed during initial setup.)

### SetupStatus
**Description**: A mechanism to persistently track the completion status of the initial event setup.
**Attributes**:
- `isInitialSetupComplete`: Boolean (NEW - default `false`. Set to `true` once the initial setup process has successfully finished.)

## Relationships
- No direct relationships between `Event` and `SetupStatus` entities. `SetupStatus` is a global flag.

## Validation Rules
- `Event.isSeen` should be `false` by default and updated to `true` during the initial setup process.
- `SetupStatus.isInitialSetupComplete` should be `false` initially and set to `true` only upon successful completion of the entire initial setup process.

## State Transitions
- `SetupStatus.isInitialSetupComplete`: `false` -> `true` (once, after successful initial setup).
