package dev.zala.restapi.controller

import dev.zala.restapi.model.Book
import dev.zala.restapi.model.BookRating
import dev.zala.restapi.model.BookStatus
import dev.zala.restapi.model.DetailedRating
import dev.zala.restapi.model.DetailedRatingValidator
import dev.zala.restapi.model.ReadingStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST API for a home library — managing your books.
 *
 * Design idea: URLs are "nouns" (resources), HTTP methods are "verbs":
 *   GET    = read
 *   POST   = create (or do an action, see lend/return below)
 *   PUT    = replace/update
 *   DELETE = remove
 *
 * Base path: /api/books
 */
@RestController
@RequestMapping("/api/books")
class BookController {

    private val books = mutableListOf(
        Book(
            id = 1L,
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            description = "A fantasy classic about Bilbo Baggins and his unexpected journey.",
            rating = "5",
            detailedRating = DetailedRating(character = 5.0, plot = 5.0, writing = 5.0, worldBuilding = 5.0, enjoyment = 5.0, comment = "Timeless classic."),
            readingStatus = ReadingStatus.READ,
            isbn = "978-0547928227",
            year = 1937,
            status = BookStatus.ON_SHELF,
            lentTo = null
        ),
        Book(2L, "Clean Code", "Robert C. Martin", "Practical guide to writing readable and maintainable code.", "4.5", null, ReadingStatus.READ, "978-0132350884", 2008, BookStatus.LENT_OUT, "Alice"),
        Book(3L, "Dune", "Frank Herbert", null, null, null, ReadingStatus.WANT_TO_READ_OWN, "978-0441172719", 1965, BookStatus.ON_SHELF, null),
        Book(4L, "Project Hail Mary", "Andy Weir", null, null, null, ReadingStatus.CURRENTLY_READING, null, 2021, BookStatus.ON_SHELF, null)
    )

    // ---------- CRUD (Create, Read, Update, Delete) ----------

    /**
     * GET /api/books
     * List all books. You can add query params later (e.g. ?author=Tolkien).
     */
    @GetMapping
    fun list(
        @RequestParam(required = false) author: String? = null,
        @RequestParam(required = false) status: BookStatus? = null,
        @RequestParam(required = false) readingStatus: ReadingStatus? = null
    ): List<Book> {
        var result = books
        if (author != null) result = result.filter { it.author.contains(author, ignoreCase = true) }
        if (status != null) result = result.filter { it.status == status }
        if (readingStatus != null) result = result.filter { it.readingStatus == readingStatus }
        return result
    }

    /**
     * GET /api/books/{id}
     * Get one book by id. Return 404 if not found.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Book> {
        val book = books.find { it.id == id }
        return if (book != null) ResponseEntity.ok(book)
        else ResponseEntity.notFound().build()
    }

    /**
     * POST /api/books
     * Add a new book. Client sends JSON body; we assign id and default status.
     */
    @PostMapping
    fun create(@RequestBody req: CreateBookRequest): ResponseEntity<Any> {
        if (!BookRating.isValid(req.rating)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid rating. Use DNF, or 1–5 in .25 steps (e.g. 4.5, 3.25)"))
        }
        req.detailedRating?.let { dr ->
            DetailedRatingValidator.validate(dr)?.let { err ->
                return ResponseEntity.badRequest().body(mapOf("error" to err))
            }
        }
        val newId = (books.maxOfOrNull { it.id } ?: 0L) + 1
        val book = Book(
            id = newId,
            title = req.title,
            author = req.author,
            description = req.description,
            rating = req.rating,
            detailedRating = req.detailedRating,
            readingStatus = req.readingStatus,
            isbn = req.isbn,
            year = req.year,
            status = BookStatus.ON_SHELF,
            lentTo = null
        )
        books.add(book)
        return ResponseEntity.ok(book)
    }

    /**
     * PUT /api/books/{id}
     * Update a book (full replace of editable fields). For a real app you might use PATCH for partial updates.
     */
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: UpdateBookRequest): ResponseEntity<Any> {
        if (!BookRating.isValid(req.rating)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid rating. Use DNF, or 1–5 in .25 steps (e.g. 4.5, 3.25)"))
        }
        req.detailedRating?.let { dr ->
            DetailedRatingValidator.validate(dr)?.let { err ->
                return ResponseEntity.badRequest().body(mapOf("error" to err))
            }
        }
        val index = books.indexOfFirst { it.id == id }
        if (index < 0) return ResponseEntity.notFound().build()
        val updated = books[index].copy(
            title = req.title,
            author = req.author,
            description = req.description,
            rating = req.rating,
            detailedRating = req.detailedRating,
            readingStatus = req.readingStatus,
            isbn = req.isbn,
            year = req.year
        )
        books[index] = updated
        return ResponseEntity.ok(updated)
    }

    /**
     * DELETE /api/books/{id}
     * Remove a book from the library.
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        val removed = books.removeIf { it.id == id }
        return if (removed) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    // ---------- Library actions (beyond basic CRUD) ----------

    /**
     * POST /api/books/{id}/lend
     * "Lend" is an action on a book, so we model it as a sub-resource action: POST .../lend
     * Body: { "lentTo": "Friend's name" }
     */
    @PostMapping("/{id}/lend")
    fun lend(@PathVariable id: Long, @RequestBody req: LendRequest): ResponseEntity<Book> {
        val index = books.indexOfFirst { it.id == id }
        if (index < 0) return ResponseEntity.notFound().build()
        val book = books[index]
        if (book.status == BookStatus.LENT_OUT) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build() // 409: already lent
        }
        val updated = book.copy(status = BookStatus.LENT_OUT, lentTo = req.lentTo)
        books[index] = updated
        return ResponseEntity.ok(updated)
    }

    /**
     * POST /api/books/{id}/return
     * Return a book (put it back on the shelf). No body needed.
     */
    @PostMapping("/{id}/return")
    fun returnBook(@PathVariable id: Long): ResponseEntity<Book> {
        val index = books.indexOfFirst { it.id == id }
        if (index < 0) return ResponseEntity.notFound().build()
        val book = books[index]
        val updated = book.copy(status = BookStatus.ON_SHELF, lentTo = null)
        books[index] = updated
        return ResponseEntity.ok(updated)
    }

    // ---------- Request DTOs (what the client sends) ----------

    data class CreateBookRequest(
        val title: String,
        val author: String,
        val readingStatus: ReadingStatus,
        val description: String? = null,
        val rating: String? = null,           // simple: DNF or 1–5 in .25 steps
        val detailedRating: DetailedRating? = null,  // character, plot, writing, worldBuilding, enjoyment (1–10 each), comment
        val isbn: String? = null,
        val year: Int? = null
    )

    data class UpdateBookRequest(
        val title: String,
        val author: String,
        val readingStatus: ReadingStatus,
        val description: String? = null,
        val rating: String? = null,
        val detailedRating: DetailedRating? = null,
        val isbn: String? = null,
        val year: Int? = null
    )

    data class LendRequest(
        val lentTo: String
    )
}
