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
            SuggestionsConfigScheme.PRE_INSTALLED to fromEnum(data.preInstalled),
            SuggestionsConfigScheme.VIEW_MODE to fromEnum(data.viewMode),
            SuggestionsConfigScheme.SORT to strSort,
            SuggestionsConfigScheme.SORT_ORDER to strOrder,
            SuggestionsConfigScheme.ADD to fromEnum(data.add),
            SuggestionsConfigScheme.TAKE_SUGGESTIONS to fromEnum(data.takeSuggestions),
            SuggestionsConfigScheme.TAKE_DETAILS to fromEnum(data.takeDetails)
        )
    }

    override fun fromPreferences(preferences: Preferences): SuggestionsConfig {
        val preInstalled = toEnum(
            preferences[SuggestionsConfigScheme.PRE_INSTALLED],
            SuggestionsDefaults.PRE_INSTALLED
        )
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
        val add = toEnum(
            preferences[SuggestionsConfigScheme.ADD],
            SuggestionsDefaults.ADD
        )
        val takeSuggestions = toEnum(
            preferences[SuggestionsConfigScheme.TAKE_SUGGESTIONS],
            SuggestionsDefaults.TAKE_SUGGESTIONS
        )
        val takeDetails = toEnum(
            preferences[SuggestionsConfigScheme.TAKE_DETAILS],
            SuggestionsDefaults.TAKE_DETAILS
        )
        return SuggestionsConfig(
            preInstalled = preInstalled,
            viewMode = viewMode,
            sort = sort,
            add = add,
            takeSuggestions = takeSuggestions,
            takeDetails = takeDetails
        )
    }
}