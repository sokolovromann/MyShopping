package ru.sokolovromann.myshopping.core.domain.model

data class FilteredProductsByPriority(
    val high: Collection<Product>,
    val medium: Collection<Product>
)