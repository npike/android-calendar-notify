# Research

## Subscribing to Calendar Content Provider URIs and Polling

The user has requested that the worker be triggered every 15 minutes AND when `CalendarContract.Events.CONTENT_URI` is triggered.

### Implementation Details

`WorkManager` does not support combining a `PeriodicWorkRequest` (for the 15-minute interval) and a `ContentUriTrigger` in a single request. Therefore, we have two options:

1.  **Two separate `WorkRequest`s:**
    *   A `PeriodicWorkRequest` that runs every 15 minutes.
    *   A `OneTimeWorkRequest` with a `ContentUriTrigger`. This request would need to be re-enqueued each time it successfully completes its work.

2.  **A single `PeriodicWorkRequest`:**
    *   A `PeriodicWorkRequest` that runs every 15 minutes.
    *   This approach would not provide real-time updates when the calendar changes, and would rely solely on polling.

### Recommendation

The most efficient approach is to use the `ContentUriTrigger` as the primary mechanism for detecting changes. This provides real-time updates and is more battery-friendly. The 15-minute periodic fetch can be implemented as a fallback mechanism to ensure that no changes are missed, for example, if the `ContentObserver` fails for some reason.

Therefore, the recommended approach is to use two separate `WorkRequest`s:

- A `OneTimeWorkRequest` with a `ContentUriTrigger` that re-enqueues itself.
- A `PeriodicWorkRequest` that runs every 15 minutes as a fallback.

This hybrid approach will provide the best balance of real-time updates and reliability.

### Decision

We will proceed with the hybrid approach of using a `ContentUriTrigger` for real-time updates and a 15-minute `PeriodicWorkRequest` as a fallback.