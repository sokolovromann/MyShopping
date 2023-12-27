package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.ui.model.AutocompleteItem
import ru.sokolovromann.myshopping.ui.model.AutocompleteLocation
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.toUiString

object UiAutocompletesMapper {

    private const val SEPARATOR: String = ", "

    fun toLocationValue(location: AutocompleteLocation): SelectedValue<AutocompleteLocation> {
        return SelectedValue(
            selected = location,
            text = when (location) {
                AutocompleteLocation.DEFAULT -> UiString.FromResources(R.string.autocompletes_action_selectDefaultLocation)
                AutocompleteLocation.PERSONAL -> UiString.FromResources(R.string.autocompletes_action_selectPersonalLocation)
            }
        )
    }

    fun toAutocompleteItems(
        autocompletesWithConfig: AutocompletesWithConfig
    ): List<AutocompleteItem> {
        return autocompletesWithConfig.groupAutocompletesByName().map {
            AutocompleteItem(
                name = it.key.toUiString(),
                brands = toBrands(autocompletesWithConfig.distinctByBrand(it.value)),
                sizes = toSizes(autocompletesWithConfig.distinctBySize(it.value)),
                colors = toColors(autocompletesWithConfig.distinctByColor(it.value)),
                manufacturers = toManufacturers(autocompletesWithConfig.distinctByManufacturer(it.value)),
                quantities = toQuantities(autocompletesWithConfig.distinctByQuantity(it.value)),
                prices = toPrices(autocompletesWithConfig.distinctByPrice(it.value)),
                discounts = toDiscounts(autocompletesWithConfig.distinctByDiscount(it.value)),
                totals = toTotals(autocompletesWithConfig.distinctByTotal(it.value))
            )
        }
    }

    private fun toBrands(brands: List<String>): UiString {
        return if (brands.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_brands,
                brands.joinToString(SEPARATOR)
            )
        }
    }

    private fun toSizes(sizes: List<String>): UiString {
        return if (sizes.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_sizes,
                sizes.joinToString(SEPARATOR)
            )
        }
    }

    private fun toColors(colors: List<String>): UiString {
        return if (colors.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_colors,
                colors.joinToString(SEPARATOR)
            )
        }
    }

    private fun toManufacturers(manufacturers: List<String>): UiString {
        return if (manufacturers.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_manufacturers,
                manufacturers.joinToString(SEPARATOR)
            )
        }
    }

    private fun toQuantities(quantities: List<Quantity>): UiString {
        return if (quantities.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_quantities,
                quantities.joinToString(SEPARATOR)
            )
        }
    }

    private fun toPrices(prices: List<Money>): UiString {
        return if (prices.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_prices,
                prices.joinToString(SEPARATOR)
            )
        }
    }

    private fun toDiscounts(discounts: List<Money>): UiString {
        return if (discounts.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_discounts,
                discounts.joinToString(SEPARATOR)
            )
        }
    }

    private fun toTotals(totals: List<Money>): UiString {
        return if (totals.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_totals,
                totals.joinToString(SEPARATOR)
            )
        }
    }
}