package ru.sokolovromann.myshopping.settings.autocompletes

data class MaxAutocompletesNumber(
    val names: Int,
    val images: Int,
    val manufacturers: Int,
    val brands: Int,
    val sizes: Int,
    val colors: Int,
    val quantities: Int,
    val prices: Int,
    val discounts: Int,
    val taxRates: Int,
    val costs: Int
)