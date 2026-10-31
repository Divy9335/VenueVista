# API Reference

Base URL for local development: `http://localhost:8080`.

Successful controller responses use an `ApiResponse` envelope with `success`, `message`, and `data` fields. Most routes require a JWT bearer token obtained from the login endpoint. The exact authorization rules are defined in `backend/src/main/java/com/example/ticketing/config/SecurityConfig.java`.

## Authentication

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register an account |
| POST | `/api/auth/login` | Public | Authenticate and receive an auth response/token |
| GET | `/api/auth/me` | Authenticated | Return the current user |

Example login request:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "customer@ticketing.com",
  "password": "customer123"
}
```

Send the returned JWT on protected requests:

```http
Authorization: Bearer <token>
```

## Events and venues

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/events` | Public | List events |
| GET | `/api/events/{id}` | Public | Get an event |
| POST | `/api/events` | Admin or organizer | Create an event |
| PUT | `/api/events/{id}` | Admin or organizer | Update an event |
| DELETE | `/api/events/{id}` | Admin or organizer | Delete an event |
| GET | `/api/venues` | Public | List venues |
| GET | `/api/venues/{id}` | Public | Get a venue |
| POST | `/api/venues` | Admin or organizer | Create a venue |
| PUT | `/api/venues/{id}` | Admin or organizer | Update a venue |
| DELETE | `/api/venues/{id}` | Admin or organizer | Delete a venue |

Create/update request fields are defined by `EventRequest` and `VenueRequest` in the backend DTO package.

## Seats

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/venues/{venueId}/seats` | Authenticated | List venue seats |
| GET | `/api/events/{eventId}/seats` | Public | List event seats and their statuses |
| GET | `/api/events/{eventId}/available-seats` | Public | List available event seats |

## Bookings and tickets

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/bookings` | Customer | Book selected seats for an event |
| GET | `/api/bookings` | Authenticated | List bookings |
| GET | `/api/bookings/{id}` | Authenticated | Get a booking |
| GET | `/api/users/{userId}/bookings` | Authenticated | List bookings for a user ID |
| PUT | `/api/bookings/{id}/cancel` | Customer | Cancel a booking |
| GET | `/api/tickets` | Authenticated | List tickets |
| GET | `/api/tickets/{id}` | Authenticated | Get a ticket |
| GET | `/api/bookings/{bookingId}/tickets` | Authenticated | List a booking's tickets |

Booking request:

```http
POST /api/bookings
Authorization: Bearer <customer-token>
Content-Type: application/json

{
  "eventId": 1,
  "eventSeatIds": [2, 3]
}
```

Booking and payment creation are implemented as a simulated workflow. No external payment gateway is called.

## Analytics

All analytics routes require an admin or organizer role.

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/analytics/overview` | Summary metrics |
| GET | `/api/analytics/revenue` | Revenue metrics |
| GET | `/api/analytics/ticket-sales` | Ticket sales data |
| GET | `/api/analytics/popular-events` | Popular event data |

## Error behavior and current caveats

Validation and application exceptions are handled by the global exception handler. Request/response DTOs and status codes should be checked in the controller and exception packages when building a client.

The current security configuration requires authentication for booking and ticket reads, but it does not yet enforce record ownership consistently for every user-ID or booking-ID lookup. Treat this API as a development project, not a production-ready service, until authorization and integration tests cover these routes.
