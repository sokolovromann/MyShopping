package ru.sokolovromann.myshopping.old.api15.model

data class Shopping(
    val id: Int = 0,
    val position: Int = 0,
    val uid: String = "",
    val lastModified: Long = 0L,
    val name: String = "",
    val reminder: Long = 0L,
    val discount: Float = 0f,
    val discountAsPercent: Boolean = false,
    val discountProducts: String = "",
    val total: Float = 0f,
    val totalFormatted: Boolean = false,
    val budget: Float = 0f,
    val budgetProducts: String = "",
    val archived: Boolean = false,
    val deleted: Boolean = false,
    val sortBy: String = "",
    val sortAscending: Boolean = true,
    val sortFormatted: Boolean = false,
    val pinned: Boolean = false
)