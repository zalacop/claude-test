# Kotlin + Spring Boot REST API

A minimal REST API built with **Kotlin** and **Spring Boot** to learn the basics.

## What's in this project

- **Sample API** at `/api/items` — simple CRUD to learn the basics.
- **Home library API** at `/api/books` — manage books, lend/return, filter by author or status.  
  See **[docs/HOME_LIBRARY_API.md](docs/HOME_LIBRARY_API.md)** for the design and how to try it.
- In-memory storage (no database); data resets when you restart the app.
- JSON request/response via Jackson (Kotlin data classes).

## Prerequisites

- **Java 17** or later (`java -version`)
- **Gradle** (optional; the project includes a Gradle wrapper)

## Run the application

If you have the Gradle wrapper set up (e.g. after running `gradle wrapper` once):

```bash
./gradlew bootRun
```

If you have Gradle installed (e.g. `brew install gradle` or [sdk install gradle](https://sdkman.io/jdks)):

```bash
gradle bootRun
```

**First time?** If `./gradlew` fails, install Gradle, then run `gradle wrapper` in this directory. After that, `./gradlew bootRun` will work.

The API will be available at **http://localhost:8080**.

## Try the API

- **List items:**  
  `curl http://localhost:8080/api/items`

- **Get one item:**  
  `curl http://localhost:8080/api/items/1`

- **Create an item:**  
  `curl -X POST http://localhost:8080/api/items -H "Content-Type: application/json" -d '{"name":"My item","description":"Optional description"}'`

- **Delete an item:**  
  `curl -X DELETE http://localhost:8080/api/items/1`

## Project structure

```
src/main/kotlin/dev/zala/restapi/
├── RestApiApplication.kt      # Spring Boot entry point
├── controller/
│   ├── ItemController.kt      # Simple CRUD demo
│   └── BookController.kt      # Home library API
└── model/
    ├── Item.kt
    └── Book.kt                # Book + BookStatus for library
docs/
└── HOME_LIBRARY_API.md        # How the library API is designed
```

## Next steps

- Add **Spring Data JPA** and a database (e.g. H2 or Postgres).
- Add validation (`@Valid`, `javax.validation`).
- Add error handling with `@ControllerAdvice`.
- Add tests with `@WebMvcTest` or `@SpringBootTest`.
