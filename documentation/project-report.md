# VenueVista

## Project Report

**Student:** [Your name]  
**Institution:** [Institution name]  
**Course:** [Course or department]  
**Supervisor:** [Supervisor name]  
**Academic year:** [Year]

## Abstract

VenueVista is a small web application for discovering events and reserving seats. Its React frontend lets customers browse and search events, create an account or sign in, select available seats, and submit a booking. A Spring Boot REST API handles authentication, event and venue data, seat availability, bookings, tickets, and basic analytics. SQLite is used for local application data and H2 for automated tests. Payment is simulated; the system does not charge money. This report describes the implemented customer-focused prototype and its current limitations.

## 1. Introduction

Event booking involves keeping event details, venue seating, and ticket availability consistent. A web-based booking service provides one place to publish event information and lets customers reserve seats without manually coordinating with the organizer.

### 1.1 Aim

To develop a simple event ticketing prototype with a customer booking interface and a REST API backed by a relational database.

### 1.2 Objectives

- Display and search published events.
- Let a customer register or sign in.
- Show event-specific seats and their availability.
- Reserve available seats and issue ticket references.
- Store event, venue, customer, booking, ticket, and simulated payment records.
- Provide basic API endpoints for event management and sales analytics.

## 2. Scope

The finished project scope is a **customer-focused MVP**. It includes the customer flow from event discovery to booking confirmation and backend endpoints for event/venue administration. It does not include organizer/admin web screens, a booking-history page, real payment processing, or production deployment. These exclusions keep the application suitable as a compact academic prototype.

## 3. Technologies and Requirements

| Area | Technology |
|---|---|
| Frontend | React 19, Vite |
| Backend | Java 21, Spring Boot 4 |
| Persistence | Spring Data JPA, SQLite |
| Authentication | Spring Security, JWT, BCrypt |
| Automated test database | H2 |

To run locally, install Java 21 and Node.js/npm. No database server is needed: the backend creates/updates a local SQLite database file on startup. Start the backend and frontend in separate terminals as described in the project [README](../README.md).

## 4. System Design

The application uses a browser client connected to a REST API. During frontend development, Vite proxies `/api` requests to Spring Boot.

```text
Customer
   |
   v
React / Vite interface
   |  REST + JWT
   v
Spring Boot controllers
   |
   v
Services -> JPA repositories -> SQLite
                         Tests -> H2
```

### 4.1 Main data entities

| Entity | Purpose |
|---|---|
| User | Customer account and role |
| Venue | Event location and capacity |
| Seat | Physical seat belonging to a venue |
| Event | Event information, schedule, price, and venue |
| EventSeat | Availability and price of one seat for one event |
| Booking | Customer's reservation for an event |
| Ticket | Ticket reference for a booked event seat |
| Payment | Simulated payment record for a booking |

`EventSeat` keeps seat availability specific to an event. Booking a seat for one event therefore does not mark the physical venue seat as unavailable for every other event.

## 5. Implementation

The backend separates HTTP controllers, business services, repositories, entities, request/response DTOs, security, and exception handling. Public users can view events and venues. Registration creates customer accounts; login returns a signed JWT. Admin and organizer roles can use event/venue management and analytics APIs.

For booking, the client loads an event's seats, submits the selected event-seat IDs with the event ID, and receives a booking confirmation with ticket numbers and a payment reference. The backend checks that the seats belong to the event and are available, then saves the booking, tickets, seat status, and simulated payment in a transaction.

### 5.1 Main API groups

| API group | Example routes | Purpose |
|---|---|---|
| Authentication | `POST /api/auth/register`, `POST /api/auth/login` | Customer account access |
| Events | `GET /api/events`, `GET /api/events/{id}` | Browse events |
| Venues and seats | `GET /api/venues`, `GET /api/events/{id}/seats` | Browse locations and seat availability |
| Bookings | `POST /api/bookings`, `PUT /api/bookings/{id}/cancel` | Reserve or cancel seats |
| Tickets | `GET /api/bookings/{id}/tickets` | Retrieve booking tickets |
| Analytics | `GET /api/analytics/overview`, `/revenue` | Basic organizer/admin summaries |

The complete route list and example request are in the [API reference](api-reference.md).

## 6. Testing

The backend test profile uses an in-memory H2 database. The test suite passes and covers application-context startup, customer-only self-registration, and safe entity JSON serialization. Frontend Oxlint and the Vite production build also pass.

These checks do not replace full end-to-end testing. Concurrent booking behavior, all authorization boundaries, and browser-driven booking scenarios have not been comprehensively tested.

## 7. Limitations and Future Improvements

- Payments are simulated; no money is collected.
- Event/venue management and analytics are API-only; no organizer/admin screens are provided.
- Customers do not have a booking-history screen.
- The test suite should be expanded to cover booking, cancellation, ownership, and authorization.
- Database schema changes use Hibernate `update`; a deployed system should use versioned migrations and managed secrets.

## 8. Conclusion

The project delivers a compact event-ticketing MVP: customers can discover events, choose available seats, and receive a booking confirmation through a React interface backed by Spring Boot and SQLite. It meets the defined prototype scope and is runnable locally without a database server. Real payment processing, administrative screens, and broader security and integration testing remain outside this version.
