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
    val rating: String? = null,          // DNF, or 1–5 in .25 steps (simple rating)
    val detailedRating: DetailedRating? = null,  // in-depth ratings; when present, calculatedStars is derived
    val readingStatus: ReadingStatus,
    val isbn: String? = null,
    val year: Int? = null,
    val status: BookStatus = BookStatus.ON_SHELF,
    val lentTo: String? = null
) {
    /** Overall stars (1–5) calculated from detailedRating when present. Null if no detailed rating. */
    val calculatedStars: Double?
        get() = detailedRating?.toStars()
}

/**
 * In-depth rating: 1–10 in .25 steps for each category.
 * Average is computed and translated to 1–5 stars (see calculatedStars on Book).
 */
data class DetailedRating(
    val character: Double? = null,    // characters, development
    val plot: Double? = null,
    val writing: Double? = null,
    val worldBuilding: Double? = null,
    val enjoyment: Double? = null,
    val comment: String? = null      // thoughts, what you liked/didn't like
) {
    /** Averages the 5 ratings and scales to 1–5 stars, rounded to nearest .25 */
    fun toStars(): Double? {
        val values = listOf(character, plot, writing, worldBuilding, enjoyment).filterNotNull()
        if (values.isEmpty()) return null
        val avg = values.average()
        val stars = (avg / 10) * 5  // scale 1–10 → 1–5
        return kotlin.math.round(stars * 4).toDouble() / 4  // round to nearest .25
    }
}

object DetailedRatingValidator {
    /** Valid: 1–10 in .25 steps (1, 1.25, 1.5, ... 10) */
    fun isValid(value: Double?): Boolean {
        if (value == null) return true
        if (value < 1 || value > 10) return false
        val steps = (value - 1) * 4
        return steps == steps.toLong().toDouble()
    }

    fun validate(r: DetailedRating): String? {
        if (!isValid(r.character)) return "character must be 1–10 in .25 steps"
        if (!isValid(r.plot)) return "plot must be 1–10 in .25 steps"
        if (!isValid(r.writing)) return "writing must be 1–10 in .25 steps"
        if (!isValid(r.worldBuilding)) return "worldBuilding must be 1–10 in .25 steps"
        if (!isValid(r.enjoyment)) return "enjoyment must be 1–10 in .25 steps"
        return null
    }
}

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
