package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.ui.model.AutocompleteItem
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.toUiString
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DiscountType
import java.text.DecimalFormat

object UiAutocompletesMapper {

    private const val SEPARATOR: String = ", "

    fun toAutocompleteItems(
        suggestionsWithDetails: Collection<SuggestionWithDetails>,
        currency: Currency,
        quantityDecimalFormat: DecimalFormat,
        moneyDecimalFormat: DecimalFormat
    ): List<AutocompleteItem> {
        return suggestionsWithDetails.map { (suggestion, details) ->
            AutocompleteItem(
                uid = suggestion.uid,
                name = suggestion.name.toUiString(),
                brands = toBrands(details.brands),
                sizes = toSizes(details.sizes),
                colors = toColors(details.colors),
                manufacturers = toManufacturers(details.manufacturers),
                quantities = toQuantities(details.quantities, quantityDecimalFormat),
                prices = toPrices(details.unitPrices, currency, moneyDecimalFormat),
                discounts = toDiscounts(details.discounts, currency, moneyDecimalFormat),
                totals = toTotals(details.costs, currency, moneyDecimalFormat)
            )
        }
    }

    fun toUiNamesWithUids(names: Collection<Suggestion>): Collection<Pair<String, UID>> {
        return names.map { Pair(it.name, it.uid) }
    }

    fun toUiNames(names: Collection<Suggestion>): Collection<String> {
        return names.map { it.name }
    }

    fun toUiBrandsWithUids(brands: Collection<SuggestionDetail.Brand>): Collection<Pair<String, UID>> {
        return brands.map { Pair(it.value.data, it.value.uid) }
    }

    fun toUiBrands(brands: Collection<SuggestionDetail.Brand>): Collection<String> {
        return brands.map { it.value.data }
    }

    fun toUiSizesWithUids(sizes: Collection<SuggestionDetail.Size>): Collection<Pair<String, UID>> {
        return sizes.map { Pair(it.value.data, it.value.uid) }
    }

    fun toUiSizes(sizes: Collection<SuggestionDetail.Size>): Collection<String> {
        return sizes.map { it.value.data }
    }

    fun toUiColorsWithUids(colors: Collection<SuggestionDetail.Color>): Collection<Pair<String, UID>> {
        return colors.map { Pair(it.value.data, it.value.uid) }
    }

    fun toUiColors(colors: Collection<SuggestionDetail.Color>): Collection<String> {
        return colors.map { it.value.data }
    }

    fun toUiManufacturersWithUids(manufacturers: Collection<SuggestionDetail.Manufacturer>): Collection<Pair<String, UID>> {
        return manufacturers.map { Pair(it.value.data, it.value.uid) }
    }

    fun toUiManufacturers(manufacturers: Collection<SuggestionDetail.Manufacturer>): Collection<String> {
        return manufacturers.map { it.value.data }
    }

    fun toUiQuantitiesWithUids(
        quantities: Collection<SuggestionDetail.Quantity>,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, UID>> {
        return quantities.map {
            val data = it.value.data
            val quantity = data.decimal.toBigDecimalOrZero()
            val formatted = "${decimalFormat.format(quantity)} ${data.params}"
            Pair(formatted, it.value.uid)
        }
    }

    fun toUiQuantities(
        quantities: Collection<SuggestionDetail.Quantity>,
        decimalFormat: DecimalFormat
    ): Collection<Triple<String, String, String>> {
        return quantities.map {
            val data = it.value.data
            val quantity = data.decimal.toBigDecimalOrZero()
            val formatted = "${decimalFormat.format(quantity)} ${data.params}"
            Triple(formatted, data.decimal.toString(), data.params)
        }
    }

    fun toUiQuantitiesSymbols(quantities: Collection<SuggestionDetail.Quantity>): Collection<String> {
        return quantities
            .map { it.value.data.params }
            .distinct()
    }

    fun toUiPricesWithUids(
        prices: Collection<SuggestionDetail.UnitPrice>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, UID>> {
        return prices.map {
            val price = formatMoney(it.value.data, currency, decimalFormat)
            Pair(price, it.value.uid)
        }
    }

    fun toUiPrices(
        prices: Collection<SuggestionDetail.UnitPrice>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, String>> {
        return prices.map {
            val formatted = formatMoney(it.value.data, currency, decimalFormat)
            Pair(formatted, it.value.data.toString())
        }
    }

    fun toUiDiscountsWithUids(
        discounts: Collection<SuggestionDetail.Discount>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, UID>> {
        return discounts.map {
            val discount = formatDiscount(it, currency, decimalFormat)
            Pair(discount, it.value.uid)
        }
    }

    fun toUiDiscounts(
        discounts: Collection<SuggestionDetail.Discount>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Triple<String, String, DiscountType>> {
        return discounts.map {
            val formatted = formatDiscount(it, currency, decimalFormat)
            val data = it.value.data
            Triple(formatted, data.decimal.toString(), data.params)
        }
    }

    fun toUiTotalsWithUids(
        totals: Collection<SuggestionDetail.Cost>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, UID>> {
        return totals.map {
            val total = formatMoney(it.value.data, currency, decimalFormat)
            Pair(total, it.value.uid)
        }
    }

    fun toUiTotals(
        totals: Collection<SuggestionDetail.Cost>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): Collection<Pair<String, String>> {
        return totals.map {
            val formatted = formatMoney(it.value.data, currency, decimalFormat)
            Pair(formatted, it.value.data.toString())
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

    private fun toQuantities(
        quantities: Collection<SuggestionDetail.Quantity>,
        decimalFormat: DecimalFormat
    ): UiString {
        return if (quantities.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_quantities,
                quantities.joinToString(SEPARATOR) {
                    val data = it.value.data
                    val quantity = data.decimal.toBigDecimalOrZero()
                    "${decimalFormat.format(quantity)} ${data.params}"
                }
            )
        }
    }

    private fun toPrices(
        prices: Collection<SuggestionDetail.UnitPrice>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): UiString {
        return if (prices.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_prices,
                prices.joinToString(SEPARATOR) {
                    formatMoney(it.value.data, currency, decimalFormat)
                }
            )
        }
    }

    private fun toDiscounts(
        discounts: Collection<SuggestionDetail.Discount>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): UiString {
        return if (discounts.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_discounts,
                discounts.joinToString(SEPARATOR) { formatDiscount(it, currency, decimalFormat) }
            )
        }
    }

    private fun toTotals(
        totals: Collection<SuggestionDetail.Cost>,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): UiString {
        return if (totals.isEmpty()) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithArgs(
                R.string.autocompletes_body_totals,
                totals.joinToString(SEPARATOR) {
                    formatMoney(it.value.data, currency, decimalFormat)
                }
            )
        }
    }

    private fun formatMoney(
        decimal: Decimal,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): String {
        val money = decimalFormat.format(decimal.toBigDecimalOrZero())
        return if (currency.displayToLeft) {
            "${currency.symbol}$money"
        } else {
            "$money${currency.symbol}"
        }
    }

    private fun formatDiscount(
        discount: SuggestionDetail.Discount,
        currency: Currency,
        decimalFormat: DecimalFormat
    ): String {
        val data = discount.value.data
        return when (data.params) {
            DiscountType.Percent -> {
                val percent = decimalFormat.format(data.decimal.toBigDecimalOrZero())
                "$percent %"
            }
            DiscountType.Money -> {
                formatMoney(data.decimal, currency, decimalFormat)
            }
        }
    }
}