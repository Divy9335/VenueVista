# VenueVista

VenueVista is a simple event-booking MVP. Customers can browse and search events, sign in, select available seats, and receive a booking confirmation. The frontend is React/Vite; the backend is Spring Boot with SQLite storage. **You do not need MySQL.**

## Requirements

- Java 21
- Node.js and npm
- Windows PowerShell

## Run VenueVista

### 1. Start the backend

Open PowerShell in the project folder:

```powershell
Set-Location .\backend
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`. SQLite creates/updates the database at:

```text
backend\data\VenueVista.sqlite
```

No separate database installation or server is needed. This project-local file keeps VenueVista data separate from other SQLite projects. To use a different file, set `DB_URL` in the same PowerShell window before starting:

```powershell
$env:DB_URL = "jdbc:sqlite:C:/your/folder/VenueVista.sqlite"
.\mvnw.cmd spring-boot:run
```

The selected parent folder must exist and be writable. On first startup, sample data is added if the database has no users.

Demo customer login:

```text
Email: customer@ticketing.com
Password: customer123
```

### 2. Start the frontend

Open a second PowerShell window in the project folder:

```powershell
Set-Location .\frontend
npm install
npm run dev
```

Open the URL Vite prints (usually `http://localhost:5173`). Keep the backend running; Vite forwards `/api` requests to it.

## Features

- Browse and search events
- Register or sign in as a customer
- See event seats and choose available seats
- Place a booking and view ticket references
- Simulated payment confirmation; no real charge is made

## Checks

Backend tests use an in-memory H2 database:

```powershell
Set-Location .\backend
.\mvnw.cmd test
```

Frontend lint and production build:

```powershell
Set-Location .\frontend
npm run lint
npm run build
```

## Project files

- `backend/` — Spring Boot API and SQLite configuration
- `frontend/` — React customer interface
- `documentation/` — project synopsis, report, and API/setup guides

Read [`documentation/project-report.md`](documentation/project-report.md) for the short academic report.
