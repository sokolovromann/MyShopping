package ru.sokolovromann.myshopping.data39.suggestions

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.LocalPreferencesMapper
import javax.inject.Inject

class SuggestionsConfigMapper @Inject constructor() : LocalPreferencesMapper<SuggestionsConfig>() {

    override fun toPreferences(data: SuggestionsConfig): Preferences {
        var strSort: String
        var strOrder: String
        when (val sort = data.sort) {
            is SortSuggestions.Name -> {
                strSort = "Name"
                strOrder = sort.order.name
            }
            is SortSuggestions.Popularity -> {
                strSort = "Popularity"
                strOrder = sort.order.name
            }
        }
        return preferencesOf(
            SuggestionsConfigScheme.VIEW_MODE to fromEnum(data.viewMode),
            SuggestionsConfigScheme.SORT to strSort,
            SuggestionsConfigScheme.SORT_ORDER to strOrder,
            SuggestionsConfigScheme.TAKE_NAMES to fromEnum(data.takeSuggestions),
            SuggestionsConfigScheme.TAKE_DESCRIPTIONS to fromEnum(data.takeDetails.descriptions),
            SuggestionsConfigScheme.TAKE_QUANTITIES to fromEnum(data.takeDetails.quantities),
            SuggestionsConfigScheme.TAKE_MONEY to fromEnum(data.takeDetails.money)
        )
    }

    override fun fromPreferences(preferences: Preferences): SuggestionsConfig {
        val viewMode = toEnum(
            preferences[SuggestionsConfigScheme.VIEW_MODE],
            SuggestionsDefaults.VIEW_MODE
        )
        val order = toEnum(
            preferences[SuggestionsConfigScheme.SORT_ORDER],
            SuggestionsDefaults.SORT_ORDER
        )
        val sort = when (preferences[SuggestionsConfigScheme.SORT]) {
            "Name" -> SortSuggestions.Name(order)
            "Popularity" -> SortSuggestions.Popularity(order)
            else -> SuggestionsDefaults.SORT
        }
        val takeSuggestions = toEnum(
            preferences[SuggestionsConfigScheme.TAKE_NAMES],
            SuggestionsDefaults.TAKE_SUGGESTIONS
        )
        val takeSuggestionDetails = TakeSuggestionDetailsInfo(
            descriptions = toEnum(
                preferences[SuggestionsConfigScheme.TAKE_DESCRIPTIONS],
                SuggestionsDefaults.TAKE_DESCRIPTIONS
            ),
            quantities = toEnum(
                preferences[SuggestionsConfigScheme.TAKE_QUANTITIES],
                SuggestionsDefaults.TAKE_QUANTITIES
            ),
            money = toEnum( preferences[
                SuggestionsConfigScheme.TAKE_MONEY],
                SuggestionsDefaults.TAKE_MONEY
            )
        )

        return SuggestionsConfig(
            viewMode = viewMode,
            sort = sort,
            takeSuggestions = takeSuggestions,
            takeDetails = takeSuggestionDetails
        )
    }
}