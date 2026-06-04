package ru.sokolovromann.myshopping.core.domain.model

data class Purchases(
    val filteredPurchasesByPriority: FilteredPurchasesByPriority,
    val status: PurchasesStatus,
    val calculatedTotal: CartTotal?
)