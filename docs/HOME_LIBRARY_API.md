# How to design a REST API for a home library

This guide walks through the ideas behind the **home library API** in this project and how to use it.

---

## 1. Think in resources (nouns)

REST APIs expose **resources** — things you can create, read, update, and delete. For a home library, the main resource is **books**.

So we have:

- **Resource:** Book  
- **URLs:**  
  - `GET /api/books` — list books  
  - `GET /api/books/1` — get book with id 1  
  - `POST /api/books` — add a new book  
  - `PUT /api/books/1` — update book 1  
  - `DELETE /api/books/1` — remove book 1  

The **path** is the noun (book/books), the **HTTP method** is the verb (GET, POST, PUT, DELETE).

---

## 2. Model your domain

A book in your home library has:

- Identity: `id`
- Core info: `title`, `author`, optional `isbn`, `year`, `description`
- **Reading status:** `READ`, `WANT_TO_READ`, `WANT_TO_READ_OWN` (wish list), `CURRENTLY_READING`
- **Rating:** when `readingStatus` is `READ`, optional simple rating (DNF or 1–5 in .25 steps)
- **Detailed rating:** optional in-depth ratings: character, plot, writing, worldBuilding, enjoyment (each 1–10 in .25 steps), plus comment. When present, `calculatedStars` (1–5) is computed from the average.
- Physical state: `status` (`ON_SHELF`, `LENT_OUT`), `lentTo` when lent

That’s the **domain model** (see `model/Book.kt`). The API sends and receives JSON that matches this model.

---

## 3. Actions that aren’t just CRUD

Besides “create/read/update/delete”, you have real-world **actions**: “lend” and “return”.

Two common ways to expose them in REST:

**Option A — Sub-resource “action” (what we did)**  
Treat the action as a sub-path and use `POST` (because it changes state):

- `POST /api/books/1/lend`  — lend book 1 (body: `{ "lentTo": "Alice" }`)
- `POST /api/books/1/return` — return book 1

**Option B — Update with PUT/PATCH**  
Client sends the new state (e.g. `status: "LENT_OUT"`, `lentTo: "Alice"`) via `PUT /api/books/1`.  
Both are valid; Option A makes “lend” and “return” very explicit and easy to document.

---

## 4. Query parameters for filtering

Listing *all* books is `GET /api/books`. To support filtering:

- `GET /api/books?author=Tolkien` — books by author containing “Tolkien”
- `GET /api/books?status=LENT_OUT` — only lent-out books
- `GET /api/books?readingStatus=CURRENTLY_READING` — only books you’re reading
- Combine: `GET /api/books?author=Martin&readingStatus=READ`

The controller reads `@RequestParam(required = false) author`, `status`, and `readingStatus` and filters accordingly.

---

## 5. HTTP status codes

Use status codes so clients can react correctly:

- **200 OK** — success (e.g. GET, PUT, POST returning the created/updated book)
- **204 No Content** — success with no body (e.g. DELETE)
- **404 Not Found** — no book with that id
- **409 Conflict** — e.g. “lend” when the book is already lent out

Returning the right status (and optionally an error body) is part of a clear REST API.

---

## 6. Try the API (with the app running)

Start the app: `./gradlew bootRun` (or `gradle bootRun`). Then:

**List all books**
```bash
curl http://localhost:8080/api/books
```

**Filter by author**
```bash
curl "http://localhost:8080/api/books?author=Tolkien"
```

**Filter by status**
```bash
curl "http://localhost:8080/api/books?status=LENT_OUT"
```

**Get one book**
```bash
curl http://localhost:8080/api/books/1
```

**Add a book** (include `readingStatus`: READ, WANT_TO_READ, WANT_TO_READ_OWN, CURRENTLY_READING)
```bash
# Add as "read" with rating
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","readingStatus":"READ","rating":"4.5","isbn":"978-0441172719","year":1965}'

# Add to wish list (want to read + own)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Project Hail Mary","author":"Andy Weir","readingStatus":"WANT_TO_READ_OWN","year":2021}'

# Add with detailed rating (character, plot, writing, worldBuilding, enjoyment: 1–10 each; calculatedStars derived)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","readingStatus":"READ","rating":"4.5","detailedRating":{"character":8.5,"plot":9,"writing":8,"worldBuilding":10,"enjoyment":8.5,"comment":"Epic world-building, dense at times."},"year":1965}'
```

**Update a book**
```bash
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"The Hobbit","author":"J.R.R. Tolkien","readingStatus":"READ","rating":"5","isbn":"978-0547928227","year":1937}'
```

**Lend a book**
```bash
curl -X POST http://localhost:8080/api/books/1/lend \
  -H "Content-Type: application/json" \
  -d '{"lentTo":"Bob"}'
```

**Return a book**
```bash
curl -X POST http://localhost:8080/api/books/1/return
```

**Delete a book**
```bash
curl -X DELETE http://localhost:8080/api/books/1
```

---

## 7. What you could add next

- **Validation** — e.g. `title` and `author` required, `year` in a sensible range (Bean Validation with `@Valid`).
- **PATCH** — partial update (only send fields that change).
- **Search** — full-text search on title/author.
- **Database** — replace the in-memory list with Spring Data JPA and a real DB.
- **Another resource** — e.g. “shelves” and link books to shelves, or “loans” as a separate resource with date lent/returned.

The same ideas (resources, HTTP methods, status codes, query params, and action-style endpoints) apply to any REST API you build.
