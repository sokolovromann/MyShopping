package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.R

data class AutocompleteItems(
    val brandsList: List<UiText> = listOf(),
    val sizesList: List<UiText> = listOf(),
    val colorsList: List<UiText> = listOf(),
    val manufacturersList: List<UiText> = listOf(),
    val quantitiesList: List<UiText> = listOf(),
    val pricesList: List<UiText> = listOf(),
    val discountsList: List<UiText> = listOf(),
    val totalsList: List<UiText> = listOf()
) {

    fun isEmpty(): Boolean {
        return brandsList.isEmpty() &&
                sizesList.isEmpty() &&
                colorsList.isEmpty() &&
                manufacturersList.isEmpty() &&
                quantitiesList.isEmpty() &&
                pricesList.isEmpty() &&
                discountsList.isEmpty() &&
                totalsList.isEmpty()
    }

    @Composable
    fun brandsToText(): UiText {
        var brands = ""
        brandsList.forEach { brands += "${it.asCompose()}, " }

        return if (brandsList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_brands,
                brands.dropLast(2)
            )
        }
    }

    @Composable
    fun sizesToText(): UiText {
        var sizes = ""
        sizesList.forEach { sizes += "${it.asCompose()}, " }

        return if (sizesList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_sizes,
                sizes.dropLast(2)
            )
        }
    }

    @Composable
    fun colorsToText(): UiText {
        var colors = ""
        colorsList.forEach { colors += "${it.asCompose()}, " }

        return if (colorsList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_colors,
                colors.dropLast(2)
            )
        }
    }

    @Composable
    fun manufacturersToText(): UiText {
        var manufacturers = ""
        manufacturersList.forEach { manufacturers += "${it.asCompose()}, " }

        return if (manufacturersList.isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromResourcesWithArgs(
                R.string.autocompletes_body_manufacturers,
                manufacturers.dropLast(2)
            )
        }
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