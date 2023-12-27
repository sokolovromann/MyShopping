package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.Composable

data class AutocompleteItem(
    val name: UiString,
    val brands: UiString,
    val sizes: UiString,
    val colors: UiString,
    val manufacturers: UiString,
    val quantities: UiString,
    val prices: UiString,
    val discounts: UiString,
    val totals: UiString
) {

    @Composable
    fun isNotFound(): Boolean {
        return brands.isEmpty() &&
                sizes.isEmpty() &&
                colors.isEmpty() &&
                manufacturers.isEmpty() &&
                quantities.isEmpty() &&
                prices.isEmpty() &&
                discounts.isEmpty() &&
                totals.isEmpty()
    }
}