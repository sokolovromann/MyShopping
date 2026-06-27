package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.SuggestionsPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
import ru.sokolovromann.myshopping.core.domain.model.SortSuggestions
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsFieldsDisplayMode
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsView
import ru.sokolovromann.myshopping.core.domain.utils.EnumUtils

@Singleton
class SuggestionsPreferencesMapper @Inject constructor() : DataStoreMapper<SuggestionsPreferences>() {

    override fun toModel(preferences: Preferences) = SuggestionsPreferences(
        toView(
            preferences[SuggestionsPreferencesScheme.VIEW_KEY],
            preferences[SuggestionsPreferencesScheme.FIELDS_DISPLAY_MODE_KEY]
        ),
        toSort(
            preferences[SuggestionsPreferencesScheme.SORT_KEY],
            preferences[SuggestionsPreferencesScheme.SORT_BY_ASCENDING_KEY]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[SuggestionsPreferencesScheme.ADDING_MODE_KEY],
            SuggestionAddingMode.All
        ),
        EnumUtils.valueOfOrDefault(
            preferences[SuggestionsPreferencesScheme.DISPLAY_NAMES_KEY],
            DisplaySuggestionNames.Medium
        ),
        EnumUtils.valueOfOrDefault(
            preferences[SuggestionsPreferencesScheme.DISPLAY_DETAILS_KEY],
            DisplaySuggestionDetails.Medium
        )
    )

    override fun toPreferences(model: SuggestionsPreferences) = preferencesOf(
        SuggestionsPreferencesScheme.VIEW_KEY
                to model.view.javaClass.simpleName,
        SuggestionsPreferencesScheme.FIELDS_DISPLAY_MODE_KEY
                to model.view.getDisplayMode().toString(),
        SuggestionsPreferencesScheme.SORT_KEY
                to model.sort.javaClass.simpleName,
        SuggestionsPreferencesScheme.SORT_BY_ASCENDING_KEY
                to model.sort.isByAscending().toString(),
        SuggestionsPreferencesScheme.ADDING_MODE_KEY
                to model.addingMode.toString(),
        SuggestionsPreferencesScheme.DISPLAY_NAMES_KEY
                to model.displaySuggestionNames.toString(),
        SuggestionsPreferencesScheme.DISPLAY_DETAILS_KEY
                to model.displaySuggestionDetails.toString()
    )

    private fun toView(view: String?, displayMode: String?): SuggestionsView {
        val productsDisplayMode = EnumUtils.valueOfOrDefault(
            displayMode,
            SuggestionsFieldsDisplayMode.All
        )
        return when (view) {
            "List" -> SuggestionsView.List(productsDisplayMode)
            "Grid" -> SuggestionsView.Grid(productsDisplayMode)
            else -> SuggestionsView.List(productsDisplayMode)
        }
    }

    private fun toSort(sort: String?, sortByAscending: String?): SortSuggestions {
        val byAscending = sortByAscending.toBoolean()
        return when (sort) {
            "ByName" -> SortSuggestions.ByName(byAscending)
            "ByPopularity" -> SortSuggestions.ByPopularity(byAscending)
            else -> SortSuggestions.ByName(byAscending)
        }
    }
}