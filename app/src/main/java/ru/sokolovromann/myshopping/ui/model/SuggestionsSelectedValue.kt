package ru.sokolovromann.myshopping.ui.model

import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.math.DiscountType

data class SuggestionsSelectedValue(
    val suggestionUid: UID? = null,
    val names: Collection<String> = listOf(),
    val brands: Collection<String> = listOf(),
    val sizes: Collection<String> = listOf(),
    val colors: Collection<String> = listOf(),
    val manufacturers: Collection<String> = listOf(),
    val quantities: Collection<Triple<String, String, String>> = listOf(),
    val quantitySymbols: Collection<String> = listOf(),
    val displayDefaultQuantitySymbols: Boolean = true,
    val prices: Collection<Pair<String, String>> = listOf(),
    val discounts: Collection<Triple<String, String, DiscountType>> = listOf(),
    val totals: Collection<Pair<String, String>> = listOf()
)