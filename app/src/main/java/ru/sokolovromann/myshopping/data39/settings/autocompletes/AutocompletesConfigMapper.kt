package ru.sokolovromann.myshopping.data39.settings.autocompletes

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class AutocompletesConfigMapper @Inject constructor() : Mapper<Preferences, AutocompletesConfig>() {

    override fun mapEntityTo(entity: Preferences): AutocompletesConfig {
        return AutocompletesConfig(
            viewMode = mapViewModeTo(entity),
            sort = mapSortTo(entity),
            maxNumber = mapMaxNumberTo(entity)
        )
    }

    override fun mapEntityFrom(model: AutocompletesConfig): Preferences {
        return mutablePreferencesOf().apply {
            val viewMode = mapViewModeFrom(model.viewMode)
            plusAssign(viewMode)

            val sort = mapSortFrom(model.sort)
            plusAssign(sort)

            val maxNumber = mapMaxNumberFrom(model.maxNumber)
            plusAssign(maxNumber)
        }
    }

    fun mapViewModeTo(entity: Preferences): AutocompletesViewMode {
        val default = AutocompletesConfigDefaults.VIEW_MODE
        val name: AutocompletesViewModeName = EnumExtensions.valueOfOrDefault(
            entity[AutocompletesConfigScheme.VIEW_MODE],
            default.name
        )
        val params: AutocompletesViewModeParams = EnumExtensions.valueOfOrDefault(
            entity[AutocompletesConfigScheme.VIEW_MODE_PARAMS],
            default.params
        )
        return AutocompletesViewMode(name, params)
    }

    fun mapViewModeFrom(model: AutocompletesViewMode): Preferences {
        return preferencesOf(
            AutocompletesConfigScheme.VIEW_MODE to model.name.name,
            AutocompletesConfigScheme.VIEW_MODE_PARAMS to model.params.name
        )
    }

    fun mapSortTo(entity: Preferences): SortAutocompletes {
        val default = AutocompletesConfigDefaults.SORT
        val name: SortAutocompletesName = EnumExtensions.valueOfOrDefault(
            entity[AutocompletesConfigScheme.SORT],
            default.name
        )
        val params: SortAutocompletesParams = EnumExtensions.valueOfOrDefault(
            entity[AutocompletesConfigScheme.SORT_PARAMS],
            default.params
        )
        return SortAutocompletes(name, params)
    }

    fun mapSortFrom(model: SortAutocompletes): Preferences {
        return preferencesOf(
            AutocompletesConfigScheme.SORT to model.name.name,
            AutocompletesConfigScheme.SORT_PARAMS to model.params.name
        )
    }

    fun mapMaxNumberTo(entity: Preferences): MaxAutocompletesNumber {
        val default = AutocompletesConfigDefaults.MAX_NUMBER
        return MaxAutocompletesNumber(
            entity[AutocompletesConfigScheme.MAXIMUM_NAMES_NUMBER] ?: default.names,
            entity[AutocompletesConfigScheme.MAXIMUM_IMAGES_NUMBER] ?: default.images,
            entity[AutocompletesConfigScheme.MAXIMUM_MANUFACTURERS_NUMBER] ?: default.manufacturers,
            entity[AutocompletesConfigScheme.MAXIMUM_BRANDS_NUMBER] ?: default.brands,
            entity[AutocompletesConfigScheme.MAXIMUM_SIZES_NUMBER] ?: default.sizes,
            entity[AutocompletesConfigScheme.MAXIMUM_COLORS_NUMBER] ?: default.colors,
            entity[AutocompletesConfigScheme.MAXIMUM_QUANTITIES_NUMBER] ?: default.quantities,
            entity[AutocompletesConfigScheme.MAXIMUM_PRICES_NUMBER] ?: default.prices,
            entity[AutocompletesConfigScheme.MAXIMUM_DISCOUNTS_NUMBER] ?: default.discounts,
            entity[AutocompletesConfigScheme.MAXIMUM_TAX_RATES_NUMBER] ?: default.taxRates,
            entity[AutocompletesConfigScheme.MAXIMUM_COSTS_NUMBER] ?: default.costs,
        )
    }

    fun mapMaxNumberFrom(model: MaxAutocompletesNumber): Preferences {
        return preferencesOf(
            AutocompletesConfigScheme.MAXIMUM_NAMES_NUMBER to model.names,
            AutocompletesConfigScheme.MAXIMUM_IMAGES_NUMBER to model.images,
            AutocompletesConfigScheme.MAXIMUM_MANUFACTURERS_NUMBER to model.manufacturers,
            AutocompletesConfigScheme.MAXIMUM_BRANDS_NUMBER to model.brands,
            AutocompletesConfigScheme.MAXIMUM_SIZES_NUMBER to model.sizes,
            AutocompletesConfigScheme.MAXIMUM_COLORS_NUMBER to model.colors,
            AutocompletesConfigScheme.MAXIMUM_QUANTITIES_NUMBER to model.quantities,
            AutocompletesConfigScheme.MAXIMUM_PRICES_NUMBER to model.prices,
            AutocompletesConfigScheme.MAXIMUM_DISCOUNTS_NUMBER to model.discounts,
            AutocompletesConfigScheme.MAXIMUM_TAX_RATES_NUMBER to model.taxRates,
            AutocompletesConfigScheme.MAXIMUM_COSTS_NUMBER to model.costs
        )
    }
}