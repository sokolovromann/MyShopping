package ru.sokolovromann.myshopping.ui.model

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity

data class AutocompletesSelectedValue(
    val names: List<Autocomplete> = listOf(),
    val brands: List<String> = listOf(),
    val sizes: List<String> = listOf(),
    val colors: List<String> = listOf(),
    val manufacturers: List<String> = listOf(),
    val quantities: List<Quantity> = listOf(),
    val quantitySymbols: List<Quantity> = listOf(),
    val displayDefaultQuantitySymbols: Boolean = true,
    val prices: List<Money> = listOf(),
    val discounts: List<Money> = listOf(),
    val totals: List<Money> = listOf(),
    val selected: Autocomplete? = null
)