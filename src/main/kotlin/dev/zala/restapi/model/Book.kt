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
    val isbn: String? = null,       // optional; not every book has ISBN
    val year: Int? = null,          // publication year
    val status: BookStatus = BookStatus.ON_SHELF,
    val lentTo: String? = null     // who has it when status is LENT_OUT
)

/**
 * Simple state for a book. Keeps the domain logic clear.
 */
enum class BookStatus {
    ON_SHELF,   // available at home
    LENT_OUT    // someone borrowed it
}
