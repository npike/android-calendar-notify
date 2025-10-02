# Data Model

## Entities

### Calendar
Represents a calendar on the device.

**Attributes**:
- `id`: Unique identifier for the calendar.
- `name`: The name of the calendar.
- `color`: The color associated with the calendar.
- `isMonitored`: A boolean indicating whether the user has chosen to monitor this calendar.

### Event
Represents a single event from a calendar.

**Attributes**:
- `id`: Unique identifier for the event.
- `calendarId`: Foreign key referencing the Calendar entity.
- `title`: The title of the event.
- `startTime`: The start time of the event.
- `endTime`: The end time of the event.
- `isSeen`: A boolean indicating whether the user has been notified about this event.
