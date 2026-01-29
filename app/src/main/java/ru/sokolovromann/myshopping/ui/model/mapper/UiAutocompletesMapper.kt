package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.ui.model.AutocompleteItem
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.toUiString
import ru.sokolovromann.myshopping.utils.math.DiscountType

object UiAutocompletesMapper {

    private const val SEPARATOR: String = ", "

    fun toAutocompleteItems(
        suggestionsWithDetails: Collection<SuggestionWithDetails>,
        currency: Currency
    ): List<AutocompleteItem> {
        return suggestionsWithDetails.map { (suggestion, details) ->
            AutocompleteItem(
                uid = suggestion.uid,
                name = suggestion.name.toUiString(),
                brands = toBrands(details.brands),
                sizes = toSizes(details.sizes),
                colors = toColors(details.colors),
                manufacturers = toManufacturers(details.manufacturers),
                quantities = toQuantities(details.quantities),
                prices = toPrices(details.unitPrices, currency),
                discounts = toDiscounts(details.discounts, currency),
                totals = toTotals(details.costs, currency)
            )
        }
    }

    fun toAutocompleteUiNames(
        autocompletesWithConfig: AutocompletesWithConfig
    ): List<UiString> {
        return autocompletesWithConfig.getNames().map {
            UiString.FromString(it)
        }
    }

    private fun toBrands(brands: Collection<SuggestionDetail.Brand>): UiString {
        return if (brands.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_brands,
                brands.joinToString(SEPARATOR) { it.value.data }
            )
        }
    }

    private fun toSizes(sizes: Collection<SuggestionDetail.Size>): UiString {
        return if (sizes.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_sizes,
                sizes.joinToString(SEPARATOR) { it.value.data }
            )
        }
    }

    private fun toColors(colors: Collection<SuggestionDetail.Color>): UiString {
        return if (colors.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_colors,
                colors.joinToString(SEPARATOR) { it.value.data }
            )
        }
    }

    private fun toManufacturers(manufacturers: Collection<SuggestionDetail.Manufacturer>): UiString {
        return if (manufacturers.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_manufacturers,
                manufacturers.joinToString(SEPARATOR) { it.value.data }
            )
        }
    }

    private fun toQuantities(quantities: Collection<SuggestionDetail.Quantity>): UiString {
        return if (quantities.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_quantities,
                quantities.joinToString(SEPARATOR) {
                    val data = it.value.data
                    "${data.decimal} ${data.params}"
                }
            )
        }
    }

    private fun toPrices(prices: Collection<SuggestionDetail.UnitPrice>, currency: Currency): UiString {
        return if (prices.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_prices,
                prices.joinToString(SEPARATOR) {
                    val data = it.value.data.toString()
                    if (currency.displayToLeft) {
                        "${currency.symbol}$data"
                    } else {
                        "$data${currency.symbol}"
                    }
                }
            )
        }
    }

    private fun toDiscounts(discounts: Collection<SuggestionDetail.Discount>, currency: Currency): UiString {
        return if (discounts.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_discounts,
                discounts.joinToString(SEPARATOR) {
                    val data = it.value.data
                    when (data.params) {
                        DiscountType.Percent -> {
                            "${data.decimal} %"
                        }
                        DiscountType.Money -> {
                            if (currency.displayToLeft) {
                                "${currency.symbol}${data.decimal}"
                            } else {
                                "${data.decimal}${currency.symbol}"
                            }
                        }
                    }
                }
            )
        }
    }

    private fun toTotals(totals: Collection<SuggestionDetail.Cost>, currency: Currency): UiString {
        return if (totals.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_totals,
                totals.joinToString(SEPARATOR) {
                    val data = it.value.data.toString()
                    if (currency.displayToLeft) {
                        "${currency.symbol}$data"
                    } else {
                        "$data${currency.symbol}"
                    }
                }
            )
        }
    }
}