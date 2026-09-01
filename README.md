# Documentation Report — Toucan Payments Challenge

## 1. Problem Understanding & Architectural Design

The objective of this project is to build a robust, production-grade RESTful API for managing customer financial transaction lifecycles. The system handles transaction ingestion, retrieval, customer transaction lookup, and strict state-controlled status updates.

### Architectural Principles
* **Layered Architecture:** Clear separation of concerns across Web Controller Layer (`SampleController`), Service Business Logic (`TransactionServiceImplementation`), Persistence Layer (`TransactionRepository`), and Entity Data Model (`Transaction`).
* **DTO Separation:** Incoming request payloads are decoupled from the database entities via dedicated Data Transfer Objects (`CreateTransactionRequest`, `UpdateTransactionStatusRequest`).
* **Financial Precision:** Currency amounts are modeled using `java.math.BigDecimal` to eliminate floating-point rounding errors.
* **Centralized Exception Handling:** Utilizes `@RestControllerAdvice` to deliver structured and consistent error JSON responses.

---

## 2. Assumptions & Data Formats

* **Storage Engine:** In-memory H2 database (`jdbc:h2:mem:testdb`), initialized automatically for testing and local execution.
* **Transaction ID:** Client-provided unique identifier matching the pattern `^TXN-\d+$` (e.g., `TXN-001`).
* **Customer ID:** Customer identifier matching the pattern `^CUST-\d+$` (e.g., `CUST-101`).
* **Monetary Amount:** Mandatory decimal value strictly greater than `0.00`.
* **Initial State:** Newly created transactions default to `PENDING` status; `transactionStatus` is not required in the creation payload.
* **Supported Currencies (`Currency` Enum):** `INR`, `USD`, `EUR`.
* **Supported Transaction Types (`TransactionType` Enum):** `PAYMENT`, `TRANSFER`, `DEPOSIT`, `WITHDRAWAL`.

---

## 3. Validation Rules & State Machine

### 3.1 Input Validation Matrix

| Field | Type | Validation Constraints | Failure Response |
| :--- | :--- | :--- | :--- |
| `transactionId` | String | `@NotBlank`, `@Pattern(regexp = "^TXN-\\d+$")` | `400 Bad Request` |
| `customerId` | String | `@NotBlank`, `@Pattern(regexp = "^CUST-\\d+$")` | `400 Bad Request` |
| `amount` | BigDecimal | `@NotNull`, `@DecimalMin(value = "0.0", inclusive = false)` | `400 Bad Request` |
| `currency` | Enum | `@NotNull`, Must be `INR`, `USD`, or `EUR` | `400 Bad Request` |
| `transactionType` | Enum | `@NotNull`, Must be `PAYMENT`, `TRANSFER`, `DEPOSIT`, `WITHDRAWAL` | `400 Bad Request` |
| Idempotency | Business Rule | Transaction ID must be unique across the system | `400 Bad Request` (Duplicate) |

### 3.2 Transaction State Transition Rules

Status updates are strictly enforced according to the finite state machine:

```text
[*] --> PENDING : Transaction Created
PENDING --> PROCESSING : Update Status
PENDING --> CANCELLED : Update Status
PROCESSING --> COMPLETED : Update Status
PROCESSING --> FAILED : Update Status
PROCESSING --> CANCELLED : Update Status
COMPLETED --> [*] : Terminal State
FAILED --> [*] : Terminal State
CANCELLED --> [*] : Terminal State
```

* **Valid Transitions:**
  * `PENDING` ➔ `PROCESSING`, `CANCELLED`
  * `PROCESSING` ➔ `COMPLETED`, `FAILED`, `CANCELLED`
* **Terminal States:** `COMPLETED`, `FAILED`, and `CANCELLED` are immutable terminal states. Any transition attempt out of these states is rejected with an `InvalidTransactionException` (`400 Bad Request`).

---

## 4. REST API Reference

### 4.1 Endpoints Summary

| Method | Endpoint | Description | Success Code | Error Codes |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/transactions` | Create a new transaction | `200 OK` | `400` |
| **GET** | `/transactions` | Retrieve all transactions | `200 OK` | — |
| **GET** | `/transactions/{transactionId}` | Get a specific transaction by ID | `200 OK` | `404` |
| **GET** | `/transactions/customer/{customerId}`| Get all transactions for a customer | `200 OK` | `404` |
| **PATCH**| `/transactions/{id}/status` | Transition transaction status | `200 OK` | `400`, `404` |
| **PUT** | `/transactions/{id}` | Full update of existing transaction | `200 OK` | `400`, `404` |

---

### 4.2 Endpoint Payloads & Examples

#### 1. Create Transaction (`POST /transactions`)
* **Request Body:**
  ```json
  {
    "transactionId": "TXN-001",
    "customerId": "CUST-101",
    "amount": 150.00,
    "currency": "USD",
    "transactionType": "PAYMENT"
  }
  ```
* **Response Body (`200 OK`):**
  ```json
  {
    "transactionId": "TXN-001",
    "customerId": "CUST-101",
    "amount": 150.00,
    "currency": "USD",
    "transactionType": "PAYMENT",
    "transactionStatus": "PENDING"
  }
  ```

#### 2. Update Transaction Status (`PATCH /transactions/{id}/status`)
* **Request Body:**
  ```json
  {
    "transactionStatus": "PROCESSING"
  }
  ```
* **Response Body (`200 OK`):**
  ```json
  {
    "transactionId": "TXN-001",
    "customerId": "CUST-101",
    "amount": 150.00,
    "currency": "USD",
    "transactionType": "PAYMENT",
    "transactionStatus": "PROCESSING"
  }
  ```

---

## 5. Error Handling Strategy

Errors are caught by `GlobalExceptionHandler` and returned in a consistent schema:

```json
{
  "message": "Invalid status transition from COMPLETED to PENDING",
  "status": 400
}
```

| Exception | HTTP Status | Trigger Condition |
| :--- | :--- | :--- |
| `TransactionNotFoundException` | `404 Not Found` | Transaction ID or Customer transactions not found |
| `DuplicateTransactionException`| `400 Bad Request` | Attempt to register an existing `transactionId` |
| `InvalidTransactionException`  | `400 Bad Request` | Illegal state transition or ID mismatch in payload |
| `MethodArgumentNotValidException` | `400 Bad Request` | Missing required fields or regex validation failure |
| `HttpMessageNotReadableException` | `400 Bad Request` | Malformed JSON body or invalid Enum literal |

---

## 6. Testing Strategy & Execution

Integration tests are implemented using **JUnit 5**, **Spring Boot Test**, and **MockMvc** with clean database isolation (`@BeforeEach` wiping the repository).

### Test Suite Matrix (`TransactionStarterApplicationTests`):
1. `testCreateAndGetAllTransactions`: Validates transaction creation with default `PENDING` status and list retrieval.
2. `testCreateTransaction_ValidationFailure`: Confirms that invalid payloads (e.g., missing ID) return `400 Bad Request`.
3. `testCreateTransaction_DuplicateTransactionId`: Confirms duplicate `transactionId` is rejected.
4. `testGetTransactionById`: Verifies exact ID lookup.
5. `testGetTransactionsByCustomerId`: Verifies customer-based transaction filtering.
6. `testUpdateTransactionStatus_Success`: Verifies valid transition (`PENDING` ➔ `PROCESSING`).
7. `testUpdateTransactionStatus_InvalidTransition`: Verifies illegal transition (`COMPLETED` ➔ `PENDING`) returns `400 Bad Request`.
8. `testGetTransactionsByCustomerId_NotFound`: Verifies 404 response for unknown customers.
9. `testGetTransactionById_NotFound`: Verifies 404 response for unknown transaction IDs.

### Running Tests
* **Windows:**
  ```bat
  mvnw.cmd clean test
  ```
* **Linux / macOS:**
  ```bash
  ./mvnw clean test
  ```

### Test Run Output
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.transactionstarter.TransactionStarterApplicationTests
...
2026-09-01T22:18:52.622+05:30  INFO 8956 --- [           main] c.e.t.TransactionStarterApplicationTests : Started TransactionStarterApplicationTests in 5.787 seconds (process running for 7.236)
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 8.060 s -- in com.example.transactionstarter.TransactionStarterApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  14.381 s
[INFO] Finished at: 2026-09-01T22:18:54+05:30
[INFO] ------------------------------------------------------------------------
```

---

## 7. Known Limitations

* **In-Memory Volatility:** H2 stores data in RAM; all transactions reset when the application terminates.
* **Authentication & Authorization:** Endpoints are publicly accessible without authentication (JWT/OAuth2) or role-based access control.
* **Monolithic Ledger:** Operates purely as a record management system without direct webhooks or integrations into external payment gateways/banking settlement networks.

---

## 8. What I Would Improve With More Time

1. **Persistent Database:** Migrate from H2 to PostgreSQL with Flyway / Liquibase database migrations.
2. **Pagination & Filtering:** Add Spring Data `Pageable` parameters to `GET /transactions` and `GET /transactions/customer/{customerId}` to support high-volume transaction history.
3. **Enhanced Error Details:** Expand validation responses to RFC 7807 (`ProblemDetail`) specifying exact offending field names and violation messages.
4. **OpenAPI / Swagger:** Integrate `springdoc-openapi` to provide an interactive Swagger UI.
5. **Auditing & Timestamps:** Add `@CreatedDate` and `@LastModifiedDate` JPA audit fields for tracking transaction history.

---

## 9. AI Usage Disclosure

AI tools were used during development and review to assist with understanding Spring Boot concepts, reviewing the application structure, identifying potential implementation issues, and improving validation, exception handling, testing, and API design.
