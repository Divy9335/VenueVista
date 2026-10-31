# VenueVista frontend

React/Vite customer interface for the VenueVista backend.

## Run locally

Start the Spring Boot backend at `http://localhost:8080`, then run:

```powershell
npm install
npm run dev
```

Vite serves the frontend and proxies `/api` requests to the backend. Open the local URL printed by Vite (normally `http://localhost:5173`).

## Available customer flow

- Browse and search public events.
- View event seats and select available seats.
- Register a customer account or sign in.
- Book selected seats and view ticket/payment-reference confirmation.

Payments are simulated by the backend. This client does not implement organizer/admin management screens or a dedicated booking-history page.

## Checks

```powershell
npm run lint
npm run build
```
