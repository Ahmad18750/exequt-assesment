# ExeQut Assessment - Order/Payment Service

Short description
- Java Spring Boot service for cart/order/payment flows with in-memory H2 database used for tests and local development.
- Includes REST endpoints used by integration tests for creating carts, adding/removing items, checkout, payment start and payment webhooks.

Technologies
- Java 17+
- Spring Boot
- Maven
- H2 (test profile)
- Lombok
- JUnit 5 / Spring Boot Test
- IntelliJ IDEA (development)

Prerequisites
- JDK 17+
- Maven (or use project wrapper `mvnw.cmd` on Windows)
- IntelliJ IDEA 2024.3 (recommended)

Run locally (Windows)
- Build: `mvnw.cmd clean package`
- Run: `mvnw.cmd spring-boot:run`
- Tests: `mvnw.cmd test`

Important files
- `src/test/resources/import.sql` — initial test data (use identity-safe inserts; avoid explicit ids).
- `src/main/java/com/kira/exequtassesment/entity/Payment.java` — payment entity should use `java.time.LocalDateTime` with `@CreationTimestamp` / `@UpdateTimestamp` and `@Enumerated(EnumType.STRING)`.

Common endpoints used by tests
- POST `/carts` — create cart
- POST `/carts/{cartId}/items` — add items
- DELETE `/carts/{cartId}/{itemId}` — remove item
- POST `/orders/{cartId}/checkout` — checkout
- POST `/orders/{orderId}/payment/start` — start payment
- POST `/payments/webhook/{paymentId}` — payment webhook

Troubleshooting
- Unique key / primary key violation when running tests:
    - Cause: test `import.sql` inserted explicit `id` values conflicting with identity sequence.
    - Fix: in `src/test/resources/import.sql` truncate table, restart id sequence and insert without `id` (let JPA/H2 generate ids).
- `Data conversion error converting "TIMESTAMP to BINARY VARYING"`:
    - Cause: using wrong timestamp type (e.g. `java.sql.Timestamp` or binary type).
    - Fix: use `java.time.LocalDateTime` for `createdAt` / `updatedAt`, annotate with `@CreationTimestamp` / `@UpdateTimestamp`, and set `@Column(columnDefinition = "TIMESTAMP")`.
- If tests target a port, ensure nothing else listens on that port (default used in tests: `8080`).

H2 console
- If enabled in application properties, access: `http://localhost:8080/h2-console` (JDBC URL typically `jdbc:h2:mem:testdb` for tests).

Notes
- Tests run with `test` profile and load `src/test/resources/import.sql`.
- Keep database initialization id-safe and prefer generated ids for entities.
