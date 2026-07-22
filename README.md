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

## Core Architecture

```mermaid
flowchart LR
    A([Controller]) --> B([Service])
    B --> C([DAO])
    C --> D([Repository])
    D --> E[(Database)]
    B --> F([Model / Entity])
```

## System Design

### Architectural Diagram

```mermaid
flowchart TB
    User(["User (Client)"])

    subgraph SYS["AKATSUKI REPORT BASE — System Boundary"]
        direction TB

        subgraph AUTH["Authentication"]
            A1["Register"]
            A2["Login"]
            A3["JWT Token"]
        end

        subgraph DASH["Dashboard"]
            D1["User Info"]
            D2["Member List"]
            D3["User Count"]
        end

        subgraph NEWS["News Scraping"]
            N1["Fetch News"]
            N2["Jsoup Parser"]
            N3["Headlines"]
        end

        subgraph REV["Review Management"]
            R1["Create Review"]
            R2["View Reviews"]
            R3["Update Review"]
            R4["Delete Review"]
        end

        AUTH --> NEWS
        DASH --> REV
    end

    User --> AUTH
    User --> DASH
```

### Database Schema

<img src="./images/akatsukidb.png" alt="dbshema"></img>

### Class Diagram

```mermaid
classDiagram
    class User {
        -Long id
        -String email
        -String username
        -String password
    }

    class AkatsukiMember {
        -Long id
        -String name
        -String newsType
        -String newsScrapedUrl
    }

    class Review {
        -Long id
        -User user
        -AkatsukiMember akatsukiMember
        -String newsHeadline
        -String reviewText
        -Integer rating
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }

    User "1" --> "many" Review : writes
    AkatsukiMember "1" --> "many" Review : reviewed on
```

### Sequence Diagrams

**Authentication Flow:**

```mermaid
sequenceDiagram
    actor User
    participant AuthController
    participant UserService
    participant UserRepository
    participant JWT

    User->>AuthController: POST /api/auth/register
    AuthController->>UserService: register()
    UserService->>UserRepository: save()
    UserRepository-->>UserService: User entity
    UserService->>JWT: generateToken()
    JWT-->>UserService: Token
    UserService-->>AuthController: Token
    AuthController-->>User: 201 Created + Token
```

**News Review Flow:**

```mermaid
sequenceDiagram
    actor User
    participant DashboardController
    participant ReviewService
    participant Repository

    User->>DashboardController: POST /api/dashboard/reviews (JWT + body)
    DashboardController->>ReviewService: createReview()
    ReviewService->>Repository: findByUsername() [validate user]
    ReviewService->>Repository: findByName() [validate member]
    ReviewService->>ReviewService: validateRating()
    ReviewService->>Repository: save(review)
    Repository-->>ReviewService: Review entity
    ReviewService-->>DashboardController: ReviewResponse
    DashboardController-->>User: 200 OK + ReviewResponse
```

### Functional Requirements

1. **User Authentication** — Register & login with email/password, receive JWT token
2. **Role-based Access** — Protected routes require valid JWT Bearer token
3. **News Scraping** — Fetch Google News headlines for each Akatsuki member via Jsoup
4. **Review Management** — Authenticated users can create, read, update, and delete reviews on news headlines
5. **Rating System** — Reviews include a numeric rating from 1 to 5
6. **Ownership Control** — Users can only modify/delete their own reviews
7. **Member Directory** — View all Akatsuki member names and their corresponding news feeds

### Non-Functional Requirements

1. **Performance** — News scraping operates with a 5-second timeout to prevent blocking
2. **Security** — Passwords hashed with BCrypt; JWT tokens expire after 15 hours
3. **Scalability** — Stateless JWT authentication allows horizontal scaling
4. **Data Integrity** — JPA with `ddl-auto=update` ensures schema consistency with entities
5. **Maintainability** — Layered architecture (Controller → Service → Repository) for separation of concerns
6. **Usability** — RESTful API with consistent JSON response format across all endpoints

### Flaws

1. **No Pagination** — `GET /api/dashboard/reviews` returns all reviews at once, which may become slow with large datasets
2. **No Input Validation Framework** — Uses manual validation in service layer instead of `@Valid` / `@NotNull` annotations
3. **No Rate Limiting** — News scraping and review creation endpoints lack rate limiting, vulnerable to abuse
4. **No Soft Deletes** — Deleting a review permanently removes it from the database; no restore capability
5. **No Admin Role** — No elevated privileges for moderating reviews across all users
6. **Hardcoded Scraping Selector** — The CSS selector `a.svxzne` in `NewsFetchingComponent` may break if Google News changes its HTML structure

### Future Enhancements

1. **Pagination & Sorting** — Add `page`, `size`, `sort` parameters to the reviews listing endpoint
2. **Validation Annotations** — Use Jakarta `@Valid`, `@NotBlank`, `@Min`, `@Max` for cleaner request validation
3. **Role-based Authorization** — Introduce ADMIN role with ability to moderate/delete any review
4. **Soft Delete** — Add `active` boolean flag to reviews instead of hard delete
5. **Review Reactions** — Allow users to "like" or "upvote" reviews
6. **Comment Threads** — Enable nested replies under each review
7. **News Caching** — Cache scraped news headlines to reduce repeated scraping and improve response times
8. **WebSocket Notifications** — Notify users in real-time when someone reviews news they also reviewed

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
```