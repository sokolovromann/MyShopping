package ru.sokolovromann.myshopping.core.domain.model

data class Purchase(
    val uid: UID,
    val directory: CartDirectory,
    val position: Position,
    val created: TimeInMillis,
    val lastModified: TimeInMillis,
    val status: PurchaseStatus,
    val priority: CartPriority,
    val name: String,
    val reminder: CartReminder?,
    val discount: CartDiscount?,
    val total: PurchaseTotal?,
    val budget: CartBudget?,
    val note: String,
    val sortProducts: SortProducts?,
    val groupProductsByStatus: GroupProductsByStatus?,
    val filteredProductsByPriority: FilteredProductsByPriority
)