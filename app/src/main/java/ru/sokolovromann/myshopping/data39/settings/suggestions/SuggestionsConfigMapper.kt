package ru.sokolovromann.myshopping.data39.settings.suggestions

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.utils.EnumExtensions
import javax.inject.Inject

class SuggestionsConfigMapper @Inject constructor() {

    fun mapEntityTo(entity: Preferences): SuggestionsConfig {
        return SuggestionsConfig(
            viewMode = mapViewModeTo(entity),
            sort = mapSortTo(entity),
            take = mapTakeTo(entity)
        )
    }

    fun mapEntityFrom(model: SuggestionsConfig): Preferences {
        return mutablePreferencesOf().apply {
            val viewMode = mapViewModeFrom(model.viewMode)
            plusAssign(viewMode)

            val sort = mapSortFrom(model.sort)
            plusAssign(sort)

            val take = mapTakeFrom(model.take)
            plusAssign(take)
        }
    }

    fun mapViewModeTo(entity: Preferences): SuggestionsViewMode {
        return EnumExtensions.valueOfOrDefault(
            entity[SuggestionsConfigScheme.VIEW_MODE],
            SuggestionsConfigDefaults.VIEW_MODE
        )
    }

    fun mapViewModeFrom(model: SuggestionsViewMode): Preferences {
        return preferencesOf(
            SuggestionsConfigScheme.VIEW_MODE to model.name
        )
    }

    fun mapSortTo(entity: Preferences): SortSuggestions {
        val default = SuggestionsConfigDefaults.SORT
        val name: SortSuggestionsName = EnumExtensions.valueOfOrDefault(
            entity[SuggestionsConfigScheme.SORT],
            default.name
        )
        val params: SortSuggestionsParams = EnumExtensions.valueOfOrDefault(
            entity[SuggestionsConfigScheme.SORT_PARAMS],
            default.params
        )
        return SortSuggestions(name, params)
    }

    fun mapSortFrom(model: SortSuggestions): Preferences {
        return preferencesOf(
            SuggestionsConfigScheme.SORT to model.name.name,
            SuggestionsConfigScheme.SORT_PARAMS to model.params.name
        )
    }

    fun mapTakeTo(entity: Preferences): TakeSuggestions {
        val default = SuggestionsConfigDefaults.TAKE
        return TakeSuggestions(
            entity[SuggestionsConfigScheme.TAKE_NAMES]?.toIntOrNull() ?: default.names,
            entity[SuggestionsConfigScheme.TAKE_IMAGES]?.toIntOrNull() ?: default.images,
            entity[SuggestionsConfigScheme.TAKE_MANUFACTURERS]?.toIntOrNull() ?: default.manufacturers,
            entity[SuggestionsConfigScheme.TAKE_BRANDS]?.toIntOrNull() ?: default.brands,
            entity[SuggestionsConfigScheme.TAKE_SIZES]?.toIntOrNull() ?: default.sizes,
            entity[SuggestionsConfigScheme.TAKE_COLORS]?.toIntOrNull() ?: default.colors,
            entity[SuggestionsConfigScheme.TAKE_QUANTITIES]?.toIntOrNull() ?: default.quantities,
            entity[SuggestionsConfigScheme.TAKE_UNIT_PRICES]?.toIntOrNull() ?: default.unitPrices,
            entity[SuggestionsConfigScheme.TAKE_DISCOUNTS]?.toIntOrNull() ?: default.discounts,
            entity[SuggestionsConfigScheme.TAKE_TAX_RATES]?.toIntOrNull() ?: default.taxRates,
            entity[SuggestionsConfigScheme.TAKE_COSTS]?.toIntOrNull() ?: default.costs
        )
    }

    fun mapTakeFrom(model: TakeSuggestions): Preferences {
        return preferencesOf(
            SuggestionsConfigScheme.TAKE_NAMES to model.names.toString(),
            SuggestionsConfigScheme.TAKE_IMAGES to model.images.toString(),
            SuggestionsConfigScheme.TAKE_MANUFACTURERS to model.manufacturers.toString(),
            SuggestionsConfigScheme.TAKE_BRANDS to model.brands.toString(),
            SuggestionsConfigScheme.TAKE_SIZES to model.sizes.toString(),
            SuggestionsConfigScheme.TAKE_COLORS to model.colors.toString(),
            SuggestionsConfigScheme.TAKE_QUANTITIES to model.quantities.toString(),
            SuggestionsConfigScheme.TAKE_UNIT_PRICES to model.unitPrices.toString(),
            SuggestionsConfigScheme.TAKE_DISCOUNTS to model.discounts.toString(),
            SuggestionsConfigScheme.TAKE_TAX_RATES to model.taxRates.toString(),
            SuggestionsConfigScheme.TAKE_COSTS to model.costs.toString()
        )
    }
}