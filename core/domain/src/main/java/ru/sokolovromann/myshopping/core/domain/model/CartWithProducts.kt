package ru.sokolovromann.myshopping.core.domain.model

data class CartWithProducts(
    val cart: Cart,
    val products: Collection<Product>
)