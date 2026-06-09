package ru.sokolovromann.myshopping.core.domain.model

sealed class CalculateProductsTotal {

    data class AllProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data class CompletedProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data class ActiveProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data object DoNotCalculate : CalculateProductsTotal()

    fun getCalculatingMode(): ProductsTotalCalculatingMode? = when (this) {
        is AllProducts -> calculatingMode
        is CompletedProducts -> calculatingMode
        is ActiveProducts -> calculatingMode
        DoNotCalculate -> null
    }
}