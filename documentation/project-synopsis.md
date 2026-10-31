# VENUEVISTA
## PROJECT SYNOPSIS

**Submitted by:** [Student name]  
**Registration number:** [Registration number]  
**Programme / course:** [Programme or course]  
**Institution:** [Institution name]  
**Project supervisor:** [Supervisor name]  
**Academic year:** [Academic year]

## 1. Abstract

VenueVista is a web-based prototype for discovering events and reserving seats. Customers can browse and search events, create an account or sign in, view event-specific seat availability, select seats, and receive a booking confirmation. The frontend is built with React and Vite. A Java Spring Boot REST API provides authentication, event and venue information, booking, ticket records, and basic analytics. SQLite stores local application data, while H2 is used for automated backend tests. Payment records are simulated; the system does not process real payments.

## 2. Introduction

Event booking involves coordinating event information, venue seating, ticket availability, and customer reservations. When these activities are handled manually, information can become difficult to maintain consistently. This project provides a small centralized application in which event data and seat reservations are managed through a web interface and REST API.

The project is intentionally scoped as a customer-focused minimum viable product (MVP). It demonstrates the main booking flow without adding complex payment integration or a complete event-administration dashboard.

## 3. Problem Statement

Customers may need to contact event organizers or use disconnected processes to find event information and reserve specific seats. Organizers also need an accurate way to represent venue seats and track which seats have been reserved for each event. The project addresses this by providing a shared event catalogue and a database-backed, event-specific seat booking process.

## 4. Aim and Objectives

### Aim

To design and implement a simple web-based event ticketing prototype with customer account access, seat selection, and booking confirmation.

### Objectives

- Display and search available events.
- Allow customers to register and sign in securely.
- Present seat availability for a selected event.
- Allow customers to reserve available seats and receive ticket references.
- Persist users, venues, seats, events, bookings, tickets, and simulated payment records.
- Provide REST endpoints for event and venue operations and basic analytics.

## 5. Project Scope

The project includes a customer-facing event discovery and booking interface; a Spring Boot API; SQLite persistence; JWT authentication; event-specific seat availability; booking and ticket creation; and simulated payment records.

Event and venue management and analytics are available through backend API endpoints, but dedicated organizer/admin screens are not part of this MVP. Booking history, real payment processing, deployment automation, and production operations are also outside the current scope.

## 6. Proposed System and Methodology

The application uses a browser-based React frontend that communicates with a Spring Boot REST API. The API validates requests, applies authentication and role checks, delegates business operations to services, and persists data through Spring Data JPA repositories. During local development, the Vite server proxies API requests to the backend.

The implementation was carried out incrementally: define the domain entities and relationships; implement repositories and service logic; expose REST endpoints; add authentication and authorization; build the customer booking interface; then validate the backend and frontend with the available test, lint, and build commands.

### Booking workflow

1. The customer browses events and selects an event.
2. The frontend requests the event's seats and displays availability.
3. The customer signs in or creates a customer account.
4. The customer selects one or more available seats and submits a booking.
5. The backend verifies that the seats belong to the event and are available.
6. The backend records the booking, marks seats as booked, creates ticket references, and records a simulated payment.
7. The frontend displays the booking confirmation and payment reference.

## 7. System Architecture and Main Modules

**Architecture:** React/Vite client → Spring Boot REST controllers → service layer → Spring Data JPA repositories → SQLite database. Automated backend tests use H2.

Main backend modules include authentication and security, event and venue management, seat availability, booking and ticket handling, analytics, data repositories, request/response DTOs, and centralized exception handling.

The `EventSeat` entity links a physical venue seat to a particular event. This allows seat availability to differ between events held at the same venue.

## 8. Technology Requirements

- **Frontend:** React 19 and Vite
- **Backend:** Java 21 and Spring Boot 4
- **Database:** SQLite for local runtime; H2 for tests
- **Persistence:** Spring Data JPA and Hibernate
- **Security:** Spring Security, JWT bearer tokens, and BCrypt password hashing
- **Development tools:** Node.js/npm and Maven Wrapper

## 9. Expected Results

The expected outcome is a runnable prototype in which a customer can discover events, select available seats, and receive confirmation after a booking is stored. The backend provides structured REST endpoints and persistent domain records. The project also demonstrates layered application structure, relational entity modelling, token-based authentication, and a basic automated verification workflow.

## 10. Testing and Current Status

The backend tests use an in-memory H2 database and cover application startup, customer-only self-registration, and safe entity JSON serialization. Frontend lint and production-build checks pass. These checks do not constitute comprehensive end-to-end testing; booking scenarios, all authorization boundaries, and concurrent seat selection require further test coverage.

The current application is an academic prototype, not a production-ready ticketing service. Payments are simulated, and the frontend does not include organizer/admin management screens or a booking-history page.

## 11. Conclusion

VenueVista provides a focused demonstration of an online event booking workflow. It combines a customer-oriented React interface with a Spring Boot API and SQLite persistence. Its limited MVP scope keeps the project understandable and runnable while leaving clear opportunities for future improvement, including broader tests, administrative screens, database migrations, and integration with a payment provider.

## 12. References

1. Spring Boot Reference Documentation, https://docs.spring.io/spring-boot/
2. React Documentation, https://react.dev/
3. Vite Documentation, https://vite.dev/
4. SQLite Documentation, https://www.sqlite.org/docs.html
5. JSON Web Token (JWT), RFC 7519, https://www.rfc-editor.org/rfc/rfc7519
