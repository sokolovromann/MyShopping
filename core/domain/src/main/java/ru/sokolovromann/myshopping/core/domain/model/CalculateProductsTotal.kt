package ru.sokolovromann.myshopping.core.domain.model

sealed class CalculateProductsTotal {

    data class AllProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data class CompletedProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data class ActiveProducts(val calculatingMode: ProductsTotalCalculatingMode) : CalculateProductsTotal()

    data object DoNotCalculate : CalculateProductsTotal()
}