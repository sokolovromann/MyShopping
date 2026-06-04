package ru.sokolovromann.myshopping.core.domain.model

data class FilteredPurchasesByPriority(
    val high: Collection<Purchase>,
    val medium: Collection<Purchase>
)