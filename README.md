# Kotlin + Spring Boot REST API

A minimal REST API built with **Kotlin** and **Spring Boot** to learn the basics.

## What's in this project

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

See [docs/HOME_LIBRARY_API.md](docs/HOME_LIBRARY_API.md) for curl examples.

## Project structure

```
src/main/kotlin/dev/zala/restapi/
├── RestApiApplication.kt      # Spring Boot entry point
├── controller/
│   └── BookController.kt      # Home library API
└── model/
    └── Book.kt                # Book, DetailedRating, etc.
docs/
└── HOME_LIBRARY_API.md        # How the library API is designed
```

## Next steps

- Add **Spring Data JPA** and a database (e.g. H2 or Postgres).
- Add validation (`@Valid`, `javax.validation`).
- Add error handling with `@ControllerAdvice`.
- Add tests with `@WebMvcTest` or `@SpringBootTest`.
