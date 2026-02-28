package dev.zala.restapi.model

/**
 * Represents a book in your home library.
 *
 * REST tip: Your "resources" (nouns) are the things you expose as URLs.
 * Here the resource is "Book" — so we'll have /api/books and /api/books/{id}.
 */
data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val description: String? = null,  // short description or review
    val rating: String? = null,      // DNF, or 1–5 in .25 steps: "1", "1.25", "1.5", "1.75", "2", ... "5"
    val isbn: String? = null,        // optional; not every book has ISBN
    val year: Int? = null,          // publication year
    val status: BookStatus = BookStatus.ON_SHELF,
    val lentTo: String? = null       // who has it when status is LENT_OUT
)

/**
 * Valid rating values (in order):
 *   DNF (did not finish)
 *   1, 1.25, 1.5, 1.75, 2, 2.25, 2.5, 2.75, 3, 3.25, 3.5, 3.75, 4, 4.25, 4.5, 4.75, 5
 */
object BookRating {
    const val DNF = "DNF"

    fun isValid(rating: String?): Boolean {
        if (rating == null) return true
        if (rating.equals(DNF, ignoreCase = true)) return true
        val value = rating.toDoubleOrNull() ?: return false
        if (value < 1 || value > 5) return false
        val steps = (value - 1) * 4  // must be whole number: 0, 1, 2, ... 16
        return steps == steps.toLong().toDouble()
    }
}

/**
 * Simple state for a book. Keeps the domain logic clear.
 */
enum class BookStatus {
    ON_SHELF,   // available at home
    LENT_OUT    // someone borrowed it
}
