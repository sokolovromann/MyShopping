package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.R

data class AutocompleteItems(
    val quantitiesList: List<UiText> = listOf(),
    val pricesList: List<UiText> = listOf(),
    val discountsList: List<UiText> = listOf(),
    val totalsList: List<UiText> = listOf()
) {

    fun inEmpty(): Boolean {
        return quantitiesList.isEmpty() &&
                pricesList.isEmpty() &&
                discountsList.isEmpty() &&
                totalsList.isEmpty()
    }

    @Composable
    fun quantitiesToText(): UiText {
        var quantities = ""
        quantitiesList.forEach { quantities += "${it.asCompose()}, " }

        return if (quantitiesList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_quantities,
                quantities.dropLast(2)
            )
        }
    }

    @Composable
    fun pricesToText(): UiText {
        var prices = ""
        pricesList.forEach { prices += "${it.asCompose()}, " }

        return if (pricesList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_prices,
                prices.dropLast(2)
            )
        }
    }

    @Composable
    fun discountsToText(): UiText {
        var discounts = ""
        discountsList.forEach { discounts += "${it.asCompose()}, " }

        return if (discountsList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_discounts,
                discounts.dropLast(2)
            )
        }
    }

    @Composable
    fun totalsToText(): UiText {
        var totals = ""
        totalsList.forEach { totals += "${it.asCompose()}, " }

        return if (totalsList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_totals,
                totals.dropLast(2)
            )
        }
    }
}