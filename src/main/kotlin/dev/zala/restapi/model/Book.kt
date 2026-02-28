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
    val description: String? = null,       // short description or review
    val rating: String? = null,           // DNF, or 1–5 in .25 steps (used when readingStatus is READ)
    val readingStatus: ReadingStatus,     // where the book is in your reading journey
    val isbn: String? = null,
    val year: Int? = null,
    val status: BookStatus = BookStatus.ON_SHELF,
    val lentTo: String? = null
)

/**
 * Where a book is in your reading journey.
 */
enum class ReadingStatus {
    READ,              // finished; typically has a rating
    WANT_TO_READ,      // want to read (may or may not own)
    WANT_TO_READ_OWN,  // own it, want to read (wish list)
    CURRENTLY_READING  // in progress
}

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
 * Physical location of the book (shelf vs lent out).
 */
enum class BookStatus {
    ON_SHELF,
    LENT_OUT
}
