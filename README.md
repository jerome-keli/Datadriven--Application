# Pharmacy Stock Visibility & Reservation Platform (MVP)

> **This folder contains the files for a pharmacy stock visibility application**
> See `reports/Task1-Report.docx` for the full write-up covering system
> design, API development, and error-handling mechanisms.

A platform where customers can search which nearby pharmacies have a given
drug in stock, reserve it for pickup, and pay while pharmacists manage
their own stock through a simple dashboard API.

## Hybrid architecture: MongoDB + MySQL

Most of this domain is naturally document-shaped, so it lives in **MongoDB**:
- Drugs vary hugely by form (tablet/syrup/injection/cream), so a rigid relational
  schema would need many nullable columns or an EAV pattern.
- Stock is really a flexible per-pharmacy sub-record (quantity, batch, expiry) that
  maps cleanly onto a document.
- Reservations are safely modeled as a single document with a status field and
  embedded line items.

**Payments are the one place that genuinely needs ACID guarantees** — money
changing hands, no partial writes, no double-charging — so they live in a
separate **MySQL** table (`payments`). This is not a forced hybrid: every
other collection stayed in Mongo because Mongo is the better fit for it, and
only `payments` moved to SQL because SQL is the better fit for *it*.

The two databases are kept consistent by the application layer, not by a
database-level transaction: `ReservationService.payAndConfirm()` writes the
payment to MySQL first, and only flips the reservation to `CONFIRMED` in
MongoDB once that succeeds. If the SQL write fails, the reservation stays
`PENDING` and nothing in Mongo changes — a trade-off worth discussing in a
"Challenges and Solutions" section: there's no real distributed transaction
here, just an application-layer ordering that fails safe.

## Tech stack
- Java 21, Spring Boot 3.3 (Web, Data MongoDB, Data JPA, Validation)
- MongoDB (local or Atlas) — pharmacies, drugs, stock, reservations, users, stock_logs
- MySQL — payments
- Lightweight custom session auth (SHA-256 salted password hashing +
  in-memory bearer tokens) — see `security/` package. A production system
  would swap this for Spring Security + BCrypt/JWT.

## Running it
1. Install MongoDB locally (or Atlas) **and** MySQL locally (or a free tier
   like PlanetScale/Railway). The MySQL database is created automatically on
   first connection (`createDatabaseIfNotExist=true` in the JDBC URL) and
   Hibernate creates the `payments` table on first run.
2. Set both connection strings in `src/main/resources/application.properties`
   (`spring.data.mongodb.uri` and `spring.datasource.*`).
3. First run only: leave `app.seed.enabled=true` to populate sample data —
   110 pharmacies, 120 drug catalog entries, 1500+ stock entries, 200 users,
   150 reservations in MongoDB, and 100+ matching payment records in MySQL.
   Set it back to `false` afterwards.
4. `mvn spring-boot:run`

## MongoDB collections
| Collection    | Purpose                                              |
|---------------|-------------------------------------------------------|
| `users`       | Customers, pharmacists, admins                        |
| `pharmacies`  | Pharmacy profile + location                            |
| `drugs`       | Master drug catalog                                    |
| `stock`       | One document per (pharmacy, drug) pair                 |
| `reservations`| Customer reservations, embeds line items                |
| `stock_logs`  | Audit trail of every stock change (restock/reserve/etc)|

## MySQL table
| Table      | Purpose                                                                 |
|------------|--------------------------------------------------------------------------|
| `payments` | One row per completed/failed payment. Columns: `id, reservation_id, customer_id, pharmacy_id, amount, currency, method, status, created_at, updated_at`. `reservation_id`/`customer_id`/`pharmacy_id` are Mongo ObjectId strings — referenced, not foreign-keyed, since they live in a different database. |

## API overview

### Auth
- `POST /api/auth/register` — `{name, email, phone, password, role, pharmacyId?}`
- `POST /api/auth/login` — returns `{token, userId, role}`. Send the token as
  `Authorization: Bearer <token>` on protected endpoints.

### Public (no auth)
- `GET /api/drugs?q=paracetamol` — text search the catalog
- `GET /api/stock/availability/{drugId}` — **the core feature**: which pharmacies
  have this drug in stock, with price/quantity
- `GET /api/pharmacies?city=Accra`

### Customer (auth required)
- `POST /api/reservations` — place a reservation, decrements stock atomically
  (fails with 409 if not enough stock)
- `GET /api/reservations/mine`
- `POST /api/reservations/{id}/pay` — `{method: "CASH"|"MOBILE_MONEY"|"CARD"}`,
  writes to MySQL and confirms the reservation
- `GET /api/payments/mine` — payment history (MySQL)
- `POST /api/reservations/{id}/cancel` — only while PENDING; releases held stock

### Pharmacist (auth required, own-pharmacy only)
- `POST /api/stock` — create/update a stock entry for their pharmacy
- `DELETE /api/stock/{stockId}`
- `POST /api/stock/bulk-import` — multipart CSV upload (`drugId,quantity,price,batchNumber,expiryDate`)
  to set many drugs' stock in one call instead of one request per drug. A sample
  file is at `docs/sample-stock-import.csv`. Malformed rows are skipped and
  reported back individually rather than failing the whole upload.
- `PATCH /api/reservations/{id}/status?status=CONFIRMED` — advance a reservation
  through PENDING → CONFIRMED → READY_FOR_PICKUP → COMPLETED (or CANCELLED,
  which releases stock back)

### Admin (full CRUD, no restriction in this MVP)
- Full CRUD on `/api/pharmacies` and `/api/drugs`

## Notifications (inheritance, polymorphism, abstract class, concurrency)

Whenever a reservation is placed, paid, cancelled, or has its status advanced,
`ReservationService` calls `NotificationService`, which fans the event out to
every registered channel:

- `Notifier` — the interface every channel implements
- `AbstractNotifier` — abstract base class; implements `send()` once (formats
  the message) and declares `deliver()` abstract for subclasses to fill in
  (template method pattern)
- `EmailNotifier` / `SmsNotifier` — concrete subclasses, each overriding
  `deliver()` differently. `NotificationService` holds a `List<Notifier>`
  that Spring populates with every bean implementing the interface, then
  calls `send()` on each — the same call dispatches to different code
  depending on the concrete type (polymorphism)
- The whole fan-out runs `@Async` on a dedicated thread pool (`AsyncConfig`),
  so placing a reservation returns to the customer immediately instead of
  waiting on notification delivery — the concurrency piece

Delivery is simulated (logged to the console) rather than wired to a real
email/SMS provider — swapping in a real provider only means changing
`deliver()` inside `EmailNotifier`/`SmsNotifier`, nothing else in the app.

## Running it in Docker

```bash
docker compose up --build
```
This starts MongoDB, MySQL, and the app together, with the app configured via
environment variables (see `docker-compose.yml`) instead of the local
`application.properties`. The API is reachable at `http://localhost:8080` once
all three containers are healthy. Data seeding runs automatically on first
boot since `APP_SEED_ENABLED=true` is set in the compose file.

## Error handling
All exceptions are centralised in `exception/GlobalExceptionHandler.java`:
- `ResourceNotFoundException` → 404
- `InsufficientStockException` → 409
- `AuthException` → 401
- `ForbiddenException` → 403 (role/ownership violations)
- `PaymentException` → 409 (reservation not awaiting payment, or already paid)
- `FileProcessingException` → 400 (bulk-import file couldn't be read)
- Validation errors → 400
- `DataAccessException` (MongoDB **or** MySQL unreachable) → 503 with a
  friendly retry message — the fallback procedure for database failures
- Anything else → 500, generic message (no stack traces leaked to clients)

Reservation creation also validates every line item's stock *before* decrementing
anything, so a failure partway through never leaves stock inconsistently modified.
Bulk CSV import applies the same philosophy at the row level: one malformed
line is recorded as an error and skipped, not an all-or-nothing failure.

See `docs/QUERIES.md` for sample CRUD/retrieval/aggregation queries for the
database report.

## Concept coverage 

| Concept | Where |
|---|---|
| Inheritance | `AbstractNotifier` → `EmailNotifier`, `SmsNotifier` |
| Polymorphism | `NotificationService` calls `notifier.send(...)` on a `List<Notifier>` without knowing the concrete type |
| Interface | `Notifier` |
| Abstract class | `AbstractNotifier` (template method: `send()` concrete, `deliver()` abstract) |
| I/O operations | `StockService.bulkImport()` — reads an uploaded CSV via `BufferedReader`/`InputStreamReader` |
| Exception handling | `GlobalExceptionHandler` (all exception types), plus per-row recovery in `bulkImport()` |
| Multithreaded programming | `NotificationService.notifyReservationUpdate()` runs on a background thread via `@Async` |
| Parallelism and concurrency | `AsyncConfig`'s dedicated `ThreadPoolTaskExecutor` — multiple notification sends can run concurrently, independent of the request thread |
| Backend development with Spring Boot | Full `controller/service/repository` layering |
| Spring Boot with containerization | `Dockerfile` + `docker-compose.yml` |
| Database interaction and RESTful APIs | MongoDB + MySQL repositories, REST controllers throughout |
