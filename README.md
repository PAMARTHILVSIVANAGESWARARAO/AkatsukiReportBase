# Akatsuki Report Base

A Spring Boot application with JWT-based authentication.

## Tech Stack

- **Java 21**
- **Spring Boot 4.1.0**
- **Spring Security** (Authentication & Authorization)
- **Spring Data JPA** (Database ORM)
- **JWT (jjwt 0.12.3)** (Token-based authentication)
- **MySQL** (Database)
- **Lombok** (Boilerplate reduction)

## Architecture

```
Controller → Service → DAO → Repository → Database
                           ↓
                        Model (Entity)
```

## Authentication Flow

1. User registers with email, username, password
2. User logs in with email, password → receives JWT token (15hr expiry)
3. User accesses protected routes using Bearer token in Authorization header

## API Endpoints

---

### 1. POST /api/auth/register

Register a new user.

**Request Body:**

```json
{
  "email": "user@example.com",
  "username": "pain",
  "password": "securePassword123"
}
```

**Success Response (201 Created):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "pain",
  "message": "Registration successful!"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `400 Bad Request` | Email already registered | `{ "message": "Email already registered!" }` |
| `400 Bad Request` | Username already taken | `{ "message": "Username already taken!" }` |
| `400 Bad Request` | Missing/invalid fields | `{ "message": "...validation error..." }` |

---

### 2. POST /api/auth/login

Authenticate existing user.

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Success Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "pain",
  "message": "Login successful!"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `401 Unauthorized` | Invalid email or password | `{ "message": "Bad credentials" }` |
| `401 Unauthorized` | User not found | `{ "message": "User not found!" }` |

---

### 3. GET /api/dashboard

Access protected dashboard. Requires JWT token.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**Success Response (200 OK):**

```json
{
  "username": "pain",
  "message": "Welcome to the dashboard, pain!"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `401 Unauthorized` | No token provided | `{ "message": "Unauthorized" }` |
| `401 Unauthorized` | Invalid/expired token | `{ "message": "Unauthorized" }` |

---

## Token Details

- **Type:** Bearer JWT
- **Expiry:** 15 hours (54000000 ms)
- **Claims:** `sub` (email), `username`, `iat`, `exp`
- **Secret Key:** Configured via `jwt.secret` in `application.properties`

## Environment Configuration

Configure via `.env` file (or `application.properties`):

```properties
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/akatsuki_db
DB_USERNAME=root
DB_PASSWORD=your_password
```

## Running the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

## Example Usage (cURL)

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","username":"pain","password":"secret"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'

# Dashboard (replace TOKEN with actual JWT)
curl -X GET http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer TOKEN"
