package dev.zala.restapi.model

/**
 * Simple data class for REST API responses.
 * Jackson (used by Spring) serializes Kotlin data classes to JSON automatically.
 */
data class Item(
    val id: Long,
    val name: String,
    val description: String? = null
)
