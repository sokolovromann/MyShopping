package ru.sokolovromann.myshopping.core.domain.model

sealed class CartsView {

    data class List(val productsDisplayMode: CartsProductsDisplayMode) : CartsView()

    data class Grid(val productsDisplayMode: CartsProductsDisplayMode) : CartsView()

    fun getProductsDisplayMode() = when (this) {
        is List -> productsDisplayMode
        is Grid -> productsDisplayMode
    }
}