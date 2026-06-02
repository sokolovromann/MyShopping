package ru.sokolovromann.myshopping.core.domain.model

data class ProductsWidgetPreferences(
    val theme: Theme,
    val fontSize: FontSize,
    val sortProducts: SortProducts,
    val groupProductsByStatus: GroupProductsByStatus
)