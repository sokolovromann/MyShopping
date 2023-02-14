package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

data class Autocomplete(
    val id: Int = 0,
    val uid: String = UUID.randomUUID().toString(),
    val created: Long = System.currentTimeMillis(),
    val lastModified: Long = created,
    val name: String = "",
    val default: Boolean = false
)