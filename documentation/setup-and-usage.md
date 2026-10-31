# Setup and Usage

## Requirements

- Java 21
- Node.js and npm
- Windows PowerShell for the commands below

SQLite is included through the application dependencies. You do not need to install or run MySQL, SQLite server software, or a separate database service.

## Start the backend

From the project root:

```powershell
Set-Location .\backend
.\mvnw.cmd spring-boot:run
```

The API listens at `http://localhost:8080`. The default database is:

```text
backend\data\VenueVista.sqlite
```

This project-local file keeps other SQLite databases untouched. Hibernate uses `update` to create missing application tables the first time the backend starts.

To select another SQLite file, set `DB_URL` in the same PowerShell window:

```powershell
$env:DB_URL = "jdbc:sqlite:C:/your/folder/VenueVista.sqlite"
.\mvnw.cmd spring-boot:run
```

The folder in the selected path must exist and be writable. For a shared or deployed environment, also set a private JWT signing secret in `JWT_SECRET`.

The development seeder adds sample accounts/event data if the database contains no users:

| Role | Email | Password |
|---|---|---|
| Admin | `admin@ticketing.com` | `admin123` |
| Organizer | `organizer@ticketing.com` | `organizer123` |
| Customer | `customer@ticketing.com` | `customer123` |

These are demo credentials only.

## Start the frontend

In another PowerShell window:

```powershell
Set-Location .\frontend
npm install
npm run dev
```

Open the Vite URL (normally `http://localhost:5173`). Its `/api` proxy forwards calls to `http://localhost:8080`.

## Run checks

Backend tests use an isolated in-memory H2 database:

```powershell
Set-Location .\backend
.\mvnw.cmd test
```

Frontend checks:

```powershell
Set-Location .\frontend
npm run lint
npm run build
```
