package dev.zala.restapi.controller

import dev.zala.restapi.model.Item
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for a simple "Item" resource.
 * Base path: /api/items
 */
@RestController
@RequestMapping("/api/items")
class ItemController {

    // In-memory store for learning (replace with a database later)
    private val items = mutableListOf(
        Item(1L, "First item", "A sample item"),
        Item(2L, "Second item", "Another sample")
    )

    /**
     * GET /api/items — list all items
     */
    @GetMapping
    fun list(): List<Item> = items

    /**
     * GET /api/items/{id} — get one item by id
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Item> {
        val item = items.find { it.id == id }
        return if (item != null) ResponseEntity.ok(item)
        else ResponseEntity.notFound().build()
    }

    /**
     * POST /api/items — create a new item (send JSON in request body)
     */
    @PostMapping
    fun create(@RequestBody request: CreateItemRequest): Item {
        val newId = (items.maxOfOrNull { it.id } ?: 0L) + 1
        val item = Item(newId, request.name, request.description)
        items.add(item)
        return item
    }

    /**
     * DELETE /api/items/{id} — delete an item
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        val removed = items.removeIf { it.id == id }
        return if (removed) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    data class CreateItemRequest(
        val name: String,
        val description: String? = null
    )
}
