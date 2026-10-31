# VenueVista Technical Overview

## Technology stack

- **Backend:** Java 21, Spring Boot 4, Spring MVC, Spring Data JPA, Spring Security, Bean Validation
- **Authentication:** JWT bearer tokens and BCrypt password hashing
- **Runtime database:** SQLite with Hibernate Community Dialects
- **Test database:** H2
- **Frontend:** React 19 and Vite 8 customer booking interface

## Backend structure

The backend follows a layered structure under `com.example.ticketing`:

- `controller` exposes REST endpoints.
- `service` contains application and booking logic.
- `repository` contains Spring Data JPA persistence interfaces.
- `entity` and `enums` define persisted domain data and status values.
- `dto` defines request/response data structures.
- `security` contains JWT filtering, token operations, and user lookup.
- `config` contains Spring configuration and development seed data.
- `exception` provides application exceptions and centralized API error handling.

## Data model

The main entities are `User`, `Venue`, `Seat`, `Event`, `EventSeat`, `Booking`, `Ticket`, and `Payment`.

- A venue owns physical seats.
- An event is associated with a venue.
- `EventSeat` joins an event to a physical seat and stores event-specific availability and price. This avoids treating a venue seat as globally unavailable when it is only booked for one event.
- A booking belongs to a user and an event.
- Tickets reference the booking and the event-specific seat.
- Payment records are simulated and associated with bookings.

The JPA entities are the current source of truth for table mapping. Hibernate's `update` mode creates missing tables in `backend/data/VenueVista.sqlite`; `DB_URL` can override this. No database server is required.

## Booking flow

The booking service validates the event and requested event-seat records, calculates the total, creates a booking and ticket records, marks selected event seats as booked, and creates a simulated payment record inside a transaction. Cancellation is implemented in the booking service. This project has not yet been stress-tested for concurrent requests or payment failure scenarios.

## Security

The API is configured to be stateless and uses JWT authentication. Registration and login are public, event and venue reads are public, administrative event/venue writes are restricted to admin and organizer roles, booking creation/cancellation is limited to customers, and analytics require admin or organizer roles.

This is an initial implementation, not a completed security audit. Ownership checks for reads by booking/user IDs and comprehensive authorization tests remain necessary before deployment. The seeded account passwords and default configuration are for local demonstration only.

## Frontend

The React/Vite client supports public event listing and search, customer sign-in/registration, event-seat loading, selection of available seats, booking submission, and booking confirmation. The Vite development server proxies `/api` requests to the Spring backend. Organizer/admin management screens and a booking-history page are not implemented.

The backend's public registration endpoint now always creates a `CUSTOMER` role, regardless of a role value in the submitted request. Sensitive entity fields and inverse relationship collections are excluded from JSON serialization to avoid exposing password hashes and recursive entity graphs.

## Verification state

The backend Maven test suite has passed in the current workspace. It includes a Spring context-load smoke test with H2 and focused regression tests for customer-only registration and JSON serialization. It does not establish full functional correctness, concurrency safety, frontend integration, or production readiness.
