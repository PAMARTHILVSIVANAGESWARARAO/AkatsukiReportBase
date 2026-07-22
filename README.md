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

Access protected dashboard. Requires JWT token. Returns the authenticated user's info along with all Akatsuki member names.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**Success Response (200 OK):**

```json
{
  "username": "pain",
  "message": "Welcome to the dashboard, pain!",
  "akatsuki_members": ["itachi", "konan", "obito", "kakuzu", "sasori", "deidara", "kisame", "orichimaru", "hidan", "zetsu"]
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `401 Unauthorized` | No token provided | `{ "message": "Unauthorized" }` |
| `401 Unauthorized` | Invalid/expired token | `{ "message": "Unauthorized" }` |

---

### 4. GET /api/dashboard/user-count

Access protected user count endpoint. Requires JWT token. Returns the total number of registered users (not Akatsuki members).

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**Success Response (200 OK):**

```json
{
  "user_count": 5
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `401 Unauthorized` | No token provided | `{ "message": "Unauthorized" }` |
| `401 Unauthorized` | Invalid/expired token | `{ "message": "Unauthorized" }` |

---

### 5. POST /api/dashboard/news

Fetch scraped news for a specific Akatsuki member. Requires JWT token. Looks up the member's news URL from the database and scrapes the latest headlines.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |
| `Content-Type` | `application/json` |

**Request Body:**

```json
{
  "akatsukiMemberName": "itachi"
}
```

**Success Response (200 OK):**

```json
{
  "akatsuki_member_name": "itachi",
  "status": "success",
  "requestedUrl": "https://news.google.com/home?hl=en-IN&gl=IN&ceid=IN%3Aen",
  "title": "Google News",
  "headlines": [
    "Top Story 1",
    "Top Story 2"
  ]
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `400 Bad Request` | Missing akatsukiMemberName | `{ "message": "akatsukiMemberName is required" }` |
| `400 Bad Request` | Member not found in DB | `{ "message": "Akatsuki member not found: {name}" }` |
| `401 Unauthorized` | No token provided | `{ "message": "Unauthorized" }` |
| `401 Unauthorized` | Invalid/expired token | `{ "message": "Unauthorized" }` |

---

### 6. POST /api/dashboard/reviews

Create a new review on an Akatsuki member's news headline. Requires JWT token. Rating must be between 1 and 5.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |
| `Content-Type` | `application/json` |

**Request Body:**

```json
{
  "akatsukiMemberName": "itachi",
  "newsHeadline": "Top Story 1",
  "reviewText": "Great coverage of the latest updates!",
  "rating": 5
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "username": "pain",
  "akatsukiMemberName": "itachi",
  "newsHeadline": "Top Story 1",
  "reviewText": "Great coverage of the latest updates!",
  "rating": 5,
  "createdAt": "2025-01-15T12:30:00",
  "updatedAt": "2025-01-15T12:30:00"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `400 Bad Request` | Missing required fields | `{ "message": "reviewText is required" }` |
| `400 Bad Request` | Invalid rating | `{ "message": "rating must be between 1 and 5" }` |
| `400 Bad Request` | Member not found | `{ "message": "Akatsuki member not found: ..." }` |
| `401 Unauthorized` | No/invalid token | `{ "message": "Unauthorized" }` |

---

### 7. GET /api/dashboard/reviews

Fetch all reviews with associated user and Akatsuki member info. Requires JWT token. Results are ordered by newest first.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**Success Response (200 OK):**

```json
{
  "reviews": [
    {
      "id": 1,
      "username": "pain",
      "akatsukiMemberName": "itachi",
      "newsHeadline": "Top Story 1",
      "reviewText": "Great coverage of the latest updates!",
      "rating": 5,
      "createdAt": "2025-01-15T12:30:00",
      "updatedAt": "2025-01-15T12:30:00"
    },
    {
      "id": 2,
      "username": "konan",
      "akatsukiMemberName": "obito",
      "newsHeadline": "Breaking News",
      "reviewText": "Interesting perspective!",
      "rating": 4,
      "createdAt": "2025-01-15T12:25:00",
      "updatedAt": "2025-01-15T12:25:00"
    }
  ]
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `401 Unauthorized` | No/invalid token | `{ "message": "Unauthorized" }` |

---

### 8. PUT /api/dashboard/reviews/{id}

Update an existing review. Only the review owner can update it. Supports partial updates — only send the fields you want to change.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |
| `Content-Type` | `application/json` |

**Request Body (partial update example):**

```json
{
  "reviewText": "Updated my thoughts on this news!",
  "rating": 4
}
```

**Success Response (200 OK):**

```json
{
  "id": 1,
  "username": "pain",
  "akatsukiMemberName": "itachi",
  "newsHeadline": "Top Story 1",
  "reviewText": "Updated my thoughts on this news!",
  "rating": 4,
  "createdAt": "2025-01-15T12:30:00",
  "updatedAt": "2025-01-15T12:35:00"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `400 Bad Request` | Review not found or not owner | `{ "message": "Review not found or not authorized to update" }` |
| `400 Bad Request` | Invalid rating | `{ "message": "rating must be between 1 and 5" }` |
| `401 Unauthorized` | No/invalid token | `{ "message": "Unauthorized" }` |

---

### 9. DELETE /api/dashboard/reviews/{id}

Delete a review. Only the review owner can delete it.

**Headers:**

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**Success Response (200 OK):**

```json
{
  "message": "Review deleted successfully"
}
```

**Error Responses:**

| Status Code | Condition | Response Body |
|-------------|-----------|---------------|
| `400 Bad Request` | Review not found or not owner | `{ "message": "Review not found or not authorized to delete" }` |
| `401 Unauthorized` | No/invalid token | `{ "message": "Unauthorized" }` |

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
