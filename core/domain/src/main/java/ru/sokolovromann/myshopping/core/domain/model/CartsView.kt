package ru.sokolovromann.myshopping.core.domain.model

sealed class CartsView {

    data class List(val displayMode: CartsProductsDisplayMode) : CartsView()

    data class Grid(val displayMode: CartsProductsDisplayMode) : CartsView()

    fun getProductsDisplayMode() = when (this) {
        is List -> displayMode
        is Grid -> displayMode
    }
}