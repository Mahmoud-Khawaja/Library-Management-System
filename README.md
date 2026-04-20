# Library Management System

A RESTful API for a library system built with Spring Boot 3, H2, Spring Data JPA, MapStruct, and Lombok. Runs in Docker.

---

## Team 9 Members

| Name                              | Code    |
|-----------------------------------|---------|
| Youssef Daoud                     | 9231011 |
| Rehab Marzouk                     | 9240027 |
| Suhila Tharwat Elmasry            | 9230455 |
| Abdelrahman Reda Khalaf           | 9220428 |
| Mahmoud Mohamed Abdallah Mahmoud  | 9220795 |

## Table of Contents
- [Prerequisites](#prerequisites)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Build and Run](#build-and-run)
- [H2 Console](#h2-console)
- [API Endpoints](#api-endpoints)
- [N+1 Analysis](#n1-analysis)
- [Error Handling](#error-handling)
- [Sample curl Commands](#sample-curl-commands)

---

## Prerequisites

- Java 17 (if running without Docker)
- Maven 3.8+ (or use the included `./mvnw` wrapper)
- Docker and Docker Compose (for the containerized option)

---

## Tech Stack

| Technology      | Purpose                              |
|-----------------|--------------------------------------|
| Spring Boot 3.2 | Main application framework           |
| Spring Web      | REST controllers and HTTP handling   |
| Spring Data JPA | Repositories, pagination, sorting    |
| H2 Database     | Embedded in-memory database          |
| MapStruct 1.5   | Entity to/from DTO mapping           |
| Lombok          | Reduces boilerplate code             |
| Docker          | Multi-stage containerized build      |

---

## Project Structure

```
src/main/java/com/example/library/
  controller/
    AuthorController.java
    BookController.java
    MemberController.java
    BorrowRecordController.java
  service/
    AuthorService.java
    BookService.java
    MemberService.java
    BorrowRecordService.java
  repository/
    AuthorRepository.java
    BookRepository.java
    MemberRepository.java
    BorrowRecordRepository.java
  entity/
    Author.java
    Book.java
    Member.java
    BorrowRecord.java
  dto/
    request/   (input validation DTOs)
    response/  (API response DTOs)
  mapper/
    AuthorMapper.java
    BookMapper.java
    MemberMapper.java
    BorrowRecordMapper.java
  exception/
    ResourceNotFoundException.java
    DuplicateResourceException.java
    BookAlreadyBorrowedException.java
    GlobalExceptionHandler.java
  LibraryApplication.java
```

---

## Build and Run

### Option 1 - Docker (recommended)

```bash
# Build and start
docker-compose up --build

# Run in the background
docker-compose up --build -d

# Stop
docker-compose down
```

The app will be running at http://localhost:8080

### Option 2 - Maven

```bash
cd library-management

# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
# or
java -jar target/library-0.0.1-SNAPSHOT.jar
```

---

## H2 Console

Go to http://localhost:8080/h2-console and use these credentials:

| Field    | Value                                     |
|----------|-------------------------------------------|
| JDBC URL | `jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1` |
| Username | `sa`                                      |
| Password | (leave blank)                             |

Note: H2 is in-memory only. All data is lost when the app restarts. In a real deployment you would swap it out for PostgreSQL or MySQL.

---

## API Endpoints

### Authors - `/api/authors`

| Method | Path                    | Description                  | Status |
|--------|-------------------------|------------------------------|--------|
| GET    | `/api/authors`          | Get all authors (paginated)  | 200    |
| GET    | `/api/authors/{id}`     | Get one author by ID         | 200    |
| POST   | `/api/authors`          | Create an author             | 201    |
| PUT    | `/api/authors/{id}`     | Update an author             | 200    |
| DELETE | `/api/authors/{id}`     | Delete an author             | 204    |
| GET    | `/api/authors/{id}/books` | Get all books by an author | 200    |

### Books - `/api/books`

| Method | Path                | Description                         | Status |
|--------|---------------------|-------------------------------------|--------|
| GET    | `/api/books`        | Get all books (paginated)           | 200    |
| GET    | `/api/books/{id}`   | Get one book by ID (includes author)| 200    |
| POST   | `/api/books`        | Create a book (author must exist)   | 201    |
| PUT    | `/api/books/{id}`   | Update a book                       | 200    |
| DELETE | `/api/books/{id}`   | Delete a book                       | 204    |
| GET    | `/api/books/search` | Search by title, genre, year        | 200    |

Search query params (all optional): `?title=...&genre=...&publishedYear=...`

### Members - `/api/members`

| Method | Path                  | Description                  | Status |
|--------|-----------------------|------------------------------|--------|
| GET    | `/api/members`        | Get all members (paginated)  | 200    |
| GET    | `/api/members/{id}`   | Get one member by ID         | 200    |
| GET    | `/api/members/search` | Search by name (`?name=...`) | 200    |
| POST   | `/api/members`        | Register a new member        | 201    |
| PUT    | `/api/members/{id}`   | Update a member              | 200    |
| DELETE | `/api/members/{id}`   | Delete a member              | 204    |

### Borrow Records - `/api/borrow-records`

| Method | Path                                    | Description                    | Status |
|--------|-----------------------------------------|--------------------------------|--------|
| POST   | `/api/borrow-records`                   | Borrow a book                  | 201    |
| PUT    | `/api/borrow-records/{id}/return`       | Return a book                  | 200    |
| GET    | `/api/borrow-records/member/{memberId}` | All records for a member       | 200    |
| GET    | `/api/borrow-records/active`            | All currently borrowed books   | 200    |

### Pagination and Sorting

You can add these params to any list endpoint:

```
?page=0&size=10&sort=id,asc
?page=0&size=5&sort=lastName,desc
```

---

## N+1 Analysis

**Affected endpoint:** `GET /api/books`

**Problem:** The `Book` entity has a `@ManyToOne(fetch = FetchType.LAZY)` on `Author`. If you call `bookRepository.findAll()` and then map each book to a DTO, Hibernate runs 1 query to get all books and then fires a separate SELECT for each book's author. That is the N+1 problem.

**Fix:** `BookRepository` uses a custom JPQL query with `JOIN FETCH`:

```java
@Query(value = "SELECT b FROM Book b JOIN FETCH b.author",
       countQuery = "SELECT COUNT(b) FROM Book b")
Page<Book> findAllWithAuthorPageable(Pageable pageable);
```

This fetches all books and their authors in one query. The same approach is used in `findByIdWithAuthor`, `findByAuthorIdWithAuthor`, `searchBooks`, and all borrow record queries.

---

## Error Handling

Every error returns a structured JSON response. No stack traces are ever exposed.

```json
{
  "status": 404,
  "message": "Book not found with id: 42",
  "timestamp": "2026-04-17T10:30:00"
}
```

| HTTP Status | When it happens                                       |
|-------------|-------------------------------------------------------|
| 400         | Validation failure, bad argument, null reference      |
| 404         | Entity not found                                      |
| 409         | Book already borrowed, duplicate ISBN or email        |
| 500         | Unexpected error (still returns structured JSON)      |

---

## Sample curl Commands

```bash
# Create an author
curl -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{"firstName":"George","lastName":"Orwell","nationality":"British","birthDate":"1903-06-25"}'

# Create a book (authorId=1)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"1984","isbn":"978-0451524935","genre":"Dystopian","publishedYear":1949,"authorId":1}'

# Register a member
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"Smith","email":"alice@example.com","phoneNumber":"01012345678"}'

# Borrow a book
curl -X POST http://localhost:8080/api/borrow-records \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"memberId":1}'

# Return a book
curl -X PUT http://localhost:8080/api/borrow-records/1/return

# Search books
curl "http://localhost:8080/api/books/search?title=1984"

# Active borrows
curl http://localhost:8080/api/borrow-records/active

# Paginated book list
curl "http://localhost:8080/api/books?page=0&size=5&sort=title,asc"
```
