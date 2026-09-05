# Personal Finance Manager API

A robust, multi-tenant REST API for managing personal finances, built as a backend assignment for **Syfe**. The system allows users to track income/expenses, manage custom categories, set savings goals with dynamic progress tracking, and generate monthly/yearly financial reports.

## 🚀 Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.5.5
- **Security:** Spring Security (Session-based with Secure Cookies)
- **Database:** H2 (In-Memory) / Spring Data JPA / Hibernate
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito, JaCoCo (80%+ coverage)

## 🏗️ Architecture & Design Decisions

### 1. Layered Architecture
The application strictly follows a layered architecture to separate concerns:
`Controller (HTTP) -> Service (Business Logic) -> Repository (Persistence) -> Entity (DB)`
Request and Response DTOs are used to prevent JPA entities from leaking into the API contract.

### 2. Session-Based Authentication (No JWT)
Per assignment requirements, the app uses HTTP Sessions and Secure Cookies (`JSESSIONID`) instead of stateless JWTs.
- Passwords are hashed using **BCrypt**.
- Cookies are configured as `HttpOnly`, `Secure`, and `SameSite=Lax`.

### 3. Strict Multi-Tenant Data Isolation
Every service method enforces ownership checks. If User A attempts to read, update, or delete a Transaction, Goal, or Category belonging to User B, the API immediately returns a **403 Forbidden** status.

### 4. Financial Accuracy
All monetary values (`amount`, `targetAmount`, `netSavings`) are handled using `java.math.BigDecimal` to prevent floating-point precision errors inherent in standard `double` or `float` types.

### 5. Hard Deletions for Transactions
Transactions are hard-deleted from the database. This ensures that deleted transactions are naturally and instantly excluded from Savings Goal progress calculations and Monthly/Yearly Reports without requiring complex soft-delete filtering logic.

## ⚙️ Local Setup

### Prerequisites
- Java 17+ (JDK 21 recommended)
- Maven 3.8+ (or use the included `./mvnw` wrapper)

### Running the Application
1. Clone the repository.
2. Run the application using the Maven wrapper:
   ```bash
   COOKIE_SECURE=false ./mvnw spring-boot:run
   ```
3. The API will be available at ```http://localhost:8080```
### 🦅 API Endpoints Overview

| Feature | Endpoints |
| :--- | :--- |
| **Auth** | `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout` |
| **Categories** | `GET /api/categories`, `POST /api/categories`, `DELETE /api/categories/{name}` |
| **Transactions** | `POST /api/transactions`, `GET /api/transactions`, `PUT /api/transactions/{id}`, `DELETE /api/transactions/{id}` |
| **Goals** | `POST /api/goals`, `GET /api/goals`, `GET /api/goals/{id}`, `PUT /api/goals/{id}`, `DELETE /api/goals/{id}` |
| **Reports** | `GET /api/reports/monthly/{year}/{month}`, `GET /api/reports/yearly/{year}` |

*Full API specification and error codes (400, 401, 403, 404, 409) are handled via a `@RestControllerAdvice` Global Exception Handler.*


*Full API specification and error codes (400, 401, 403, 404, 409) are handled via a `@RestControllerAdvice` Global Exception Handler.*

## 🧪 Testing

The project includes comprehensive unit tests using **JUnit 5** and **Mockito**, ensuring >80% code coverage and validating multi-tenant isolation rules.

Run tests with coverage:

```bash
./mvnw clean test
```
## 🌐 Deployment

The application is deployed on Render and accessible at:
[https://onrender.com](https://personal-finance-manager-jmm8.onrender.com/)


## 📂 Project Structure

```text
src/main/java/com/finance/personalfinancemanager
├── auth/            # Authentication (Login, Register, UserDetails)
├── category/        # Category management & defaults
├── config/          # SecurityConfig, Seeders
├── exception/       # Custom exceptions & Global Handler
├── goal/            # Savings goals & progress calculation
├── report/          # Monthly/Yearly aggregations
├── transaction/     # Core financial transactions
├── user/            # User entity & repository
└── util/            # Security context utilities
```

