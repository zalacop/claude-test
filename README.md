# Kotlin + Spring Boot REST API

A minimal REST API built with **Kotlin** and **Spring Boot** to learn the basics.

## What's in this project

- **REST controller** at `/api/items` with:
  - `GET /api/items` — list all items
  - `GET /api/items/{id}` — get one item
  - `POST /api/items` — create an item (JSON body)
  - `DELETE /api/items/{id}` — delete an item
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
│   └── ItemController.kt      # REST endpoints
└── model/
    └── Item.kt                # Data class for items
```

## Next steps

- Add **Spring Data JPA** and a database (e.g. H2 or Postgres).
- Add validation (`@Valid`, `javax.validation`).
- Add error handling with `@ControllerAdvice`.
- Add tests with `@WebMvcTest` or `@SpringBootTest`.
