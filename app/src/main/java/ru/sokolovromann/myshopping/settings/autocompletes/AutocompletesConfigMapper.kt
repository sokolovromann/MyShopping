package ru.sokolovromann.myshopping.settings.autocompletes

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class AutocompletesConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, AutocompletesConfig>() {

    override fun mapTo(a: Preferences): AutocompletesConfig {
        val viewModeParams: AutocompletesViewMode.Params = EnumExtensions.valueOfOrDefault(
            name = a[AutocompletesConfigScheme.VIEW_MODE_PARAMS],
            defaultValue = AutocompletesConfigDefaults.VIEW_MODE.params
        )
        val viewMode: AutocompletesViewMode = AutocompletesViewMode.classOfOrDefault(
            name = a[AutocompletesConfigScheme.VIEW_MODE],
            params = viewModeParams,
            defaultValue = AutocompletesConfigDefaults.VIEW_MODE
        )
        val sortParams: SortAutocompletes.Params = EnumExtensions.valueOfOrDefault(
            name = a[AutocompletesConfigScheme.SORT_PARAMS],
            defaultValue = AutocompletesConfigDefaults.SORT.params
        )
        val sort: SortAutocompletes = SortAutocompletes.classOfOrDefault(
            name = a[AutocompletesConfigScheme.SORT],
            params = sortParams,
            defaultValue = AutocompletesConfigDefaults.SORT
        )
        val defaultMaxNumber = AutocompletesConfigDefaults.MAX_NUMBER
        val maxNumber = MaxAutocompletesNumber(
            names = a[AutocompletesConfigScheme.MAXIMUM_NAMES_NUMBER] ?: defaultMaxNumber.names,
            images = a[AutocompletesConfigScheme.MAXIMUM_IMAGES_NUMBER] ?: defaultMaxNumber.images,
            manufacturers = a[AutocompletesConfigScheme.MAXIMUM_MANUFACTURERS_NUMBER] ?: defaultMaxNumber.manufacturers,
            brands = a[AutocompletesConfigScheme.MAXIMUM_BRANDS_NUMBER] ?: defaultMaxNumber.brands,
            sizes = a[AutocompletesConfigScheme.MAXIMUM_SIZES_NUMBER] ?: defaultMaxNumber.sizes,
            colors = a[AutocompletesConfigScheme.MAXIMUM_COLORS_NUMBER] ?: defaultMaxNumber.colors,
            quantities = a[AutocompletesConfigScheme.MAXIMUM_QUANTITIES_NUMBER] ?: defaultMaxNumber.quantities,
            prices = a[AutocompletesConfigScheme.MAXIMUM_PRICES_NUMBER] ?: defaultMaxNumber.prices,
            discounts = a[AutocompletesConfigScheme.MAXIMUM_DISCOUNTS_NUMBER] ?: defaultMaxNumber.discounts,
            taxRates = a[AutocompletesConfigScheme.MAXIMUM_TAX_RATES_NUMBER] ?: defaultMaxNumber.taxRates,
            costs = a[AutocompletesConfigScheme.MAXIMUM_COSTS_NUMBER] ?: defaultMaxNumber.costs,
        )
        return AutocompletesConfig(
            viewMode = viewMode,
            sort = sort,
            maxNumber = maxNumber
        )
    }

    override fun mapFrom(b: AutocompletesConfig): Preferences {
        return preferencesOf(
            AutocompletesConfigScheme.VIEW_MODE to b.viewMode.getName(),
            AutocompletesConfigScheme.VIEW_MODE_PARAMS to b.viewMode.params.name,
            AutocompletesConfigScheme.SORT to b.sort.getName(),
            AutocompletesConfigScheme.SORT_PARAMS to b.sort.params.name,
            AutocompletesConfigScheme.MAXIMUM_NAMES_NUMBER to b.maxNumber.names,
            AutocompletesConfigScheme.MAXIMUM_IMAGES_NUMBER to b.maxNumber.images,
            AutocompletesConfigScheme.MAXIMUM_MANUFACTURERS_NUMBER to b.maxNumber.manufacturers,
            AutocompletesConfigScheme.MAXIMUM_BRANDS_NUMBER to b.maxNumber.brands,
            AutocompletesConfigScheme.MAXIMUM_SIZES_NUMBER to b.maxNumber.sizes,
            AutocompletesConfigScheme.MAXIMUM_COLORS_NUMBER to b.maxNumber.colors,
            AutocompletesConfigScheme.MAXIMUM_QUANTITIES_NUMBER to b.maxNumber.quantities,
            AutocompletesConfigScheme.MAXIMUM_PRICES_NUMBER to b.maxNumber.prices,
            AutocompletesConfigScheme.MAXIMUM_DISCOUNTS_NUMBER to b.maxNumber.discounts,
            AutocompletesConfigScheme.MAXIMUM_TAX_RATES_NUMBER to b.maxNumber.taxRates,
            AutocompletesConfigScheme.MAXIMUM_COSTS_NUMBER to b.maxNumber.costs
        )
    }
}