# Feature Specification: Initial Event Setup and Notification

**Feature Branch**: `002-this-project-already`  
**Created**: September 30, 2025  
**Status**: Draft  
**Input**: User description: "This project already contains integration with the android calendar content provider. It has repositories, contracts, data classes, integration with workmanager. There is no way to detect the created date for a calendar event in Android. On app first launch (after permissions granted) it should show the user a screen indicating that setup is happening - when app is put into background it should show that indication via a notification that cant be dismissed. The setup should be requesting all events between now and a year from now and mark them internally as "seen". The app should no longer need to keep track of the first run time."

## Execution Flow (main)
```
1. Parse user description from Input
   → If empty: ERROR "No feature description provided"
2. Extract key concepts from description
   → Identify: actors, actions, data, constraints
3. For each unclear aspect:
   → Mark with [NEEDS CLARIFICATION: specific question]
4. Fill User Scenarios & Testing section
   → If no clear user flow: ERROR "Cannot determine user scenarios"
5. Generate Functional Requirements
   → Each requirement must be testable
   → Mark ambiguous requirements
6. Identify Key Entities (if data involved)
7. Run Review Checklist
   → If any [NEEDS CLARIFICATION]: WARN "Spec has uncertainties"
   → If implementation details found: ERROR "Remove tech details"
8. Return: SUCCESS (spec ready for planning)
```

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

### Section Requirements
- **Mandatory sections**: Must be completed for every feature
- **Optional sections**: Include only when relevant to the feature
- When a section doesn't apply, remove it entirely (don't leave as "N/A")

### For AI Generation
When creating this spec from a user prompt:
1. **Mark all ambiguities**: Use [NEEDS CLARIFICATION: specific question] for any assumption you'd need to make
2. **Don't guess**: If the prompt doesn't specify something (e.g., "login system" without auth method), mark it
3. **Think like a tester**: Every vague requirement should fail the "testable and unambiguous" checklist item
4. **Common underspecified areas**:
   - User types and permissions
   - Data retention/deletion policies  
   - Performance targets and scale
   - Error handling behaviors
   - Integration requirements
   - Security/compliance needs

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
As a new user, after granting calendar permissions, I want the app to perform an initial setup by identifying the highest existing calendar event ID and persisting it, so that subsequent runs can detect only truly new events. During this setup, I want to be informed of the progress, even if the app is in the background. I also want to be able to monitor only calendars that are currently synced to my device, and see a clear indication for those that are not.

### Acceptance Scenarios
1. **Given** the app is launched for the first time and calendar permissions are granted, **When** the app starts the initial setup, **Then** a setup screen is displayed.
2. **Given** the initial setup is in progress, **When** the app is put into the background, **Then** a non-dismissible notification indicating setup progress is shown.
3. **Given** the initial setup is complete, **When** the app fetches events, **Then** the highest existing calendar event ID is identified and persisted.
4. **Given** the initial setup is complete, **When** the app is launched again, **Then** the app does not perform the initial setup again.
5. **Given** a calendar is not synced to the device, **When** the user views the calendar list, **Then** the monitoring switch for that calendar is disabled and a subtitle "This calendar is not currently being synced" is displayed.
6. **Given** a calendar is synced to the device, **When** the user views the calendar list, **Then** the monitoring switch for that calendar is enabled and on by default.
7. **Given** the calendar list is displayed, **When** the user views the list, **Then** the calendars are sorted alphabetically by name, case-insensitive.

### Edge Cases
- What happens if calendar permissions are revoked during setup? The app should stop setup and prompt the user to re-grant permissions.
- What happens if there are no events in the calendar provider during initial setup? The highest event ID should be persisted as 0.

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: The app MUST display a dedicated "setup in progress" screen on first launch after calendar permissions are granted.
- **FR-002**: The app MUST display a non-dismissible notification when the initial setup is running in the background.
- **FR-003**: The app MUST identify and persist the highest existing calendar event ID from the calendar provider during initial setup.
- **FR-004**: The app MUST NOT re-run the initial setup process after it has been successfully completed once.
- **FR-005**: The app MUST stop setup and prompt the user to re-grant permissions if calendar permissions are revoked during setup.
- **FR-006**: The app MUST disable the monitoring switch and display "This calendar is not currently being synced" for calendars not synced to the device.
- **FR-007**: The app MUST enable the monitoring switch and set it to on by default for calendars synced to the device.
- **FR-008**: The app MUST sort the calendar list alphabetically by name, case-insensitive.
- **FR-009**: The app MUST use the calendar's color for the notification icon when showing new event notifications.

## Clarifications
### Session 2025-09-30
- Q: How should the app handle permission revocation during setup? → A: Stop setup and prompt the user to re-grant permissions.
- Q: How should the app handle storage issues during event marking? → A: Ignore storage issues.

### Key Entities *(include if feature involves data)*
- **Event**: Represents a calendar event with properties like title, start time, end time, and a new internal "seen" status, and calendar color.
- **Setup Status**: A persistent indicator that tracks whether the initial setup has been completed.
- **Last Known Event ID**: A persistent indicator that stores the highest event ID seen during initial setup.

---

## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [ ] No implementation details (languages, frameworks, APIs)
- [ ] Focused on user value and business needs
- [ ] Written for non-technical stakeholders
- [ ] All mandatory sections completed

### Requirement Completeness
- [ ] No [NEEDS CLARIFICATION] markers remain
- [ ] Requirements are testable and unambiguous  
- [ ] Success criteria are measurable
- [ ] Scope is clearly bounded
- [ ] Dependencies and assumptions identified

---

## Execution Status
*Updated by main() during processing*

- [ ] User description parsed
- [ ] Key concepts extracted
- [ ] Ambiguities marked
- [ ] User scenarios defined
- [ ] Requirements generated
- [ ] Entities identified
- [ ] Review checklist passed

---