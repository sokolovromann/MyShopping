package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
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
            preferences[LocalDataStoreScheme.Suggestions.VIEW],
            preferences[LocalDataStoreScheme.Suggestions.FIELDS_DISPLAY_MODE]
        ),
        toSort(
            preferences[LocalDataStoreScheme.Suggestions.SORT],
            preferences[LocalDataStoreScheme.Suggestions.SORT_BY_ASCENDING]
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Suggestions.ADDING_MODE],
            SuggestionAddingMode.All
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Suggestions.DISPLAY_NAMES],
            DisplaySuggestionNames.Medium
        ),
        EnumUtils.valueOfOrDefault(
            preferences[LocalDataStoreScheme.Suggestions.DISPLAY_DETAILS],
            DisplaySuggestionDetails.Medium
        )
    )

    override fun toPreferences(model: SuggestionsPreferences) = preferencesOf(
        LocalDataStoreScheme.Suggestions.VIEW
                to model.view.javaClass.simpleName,
        LocalDataStoreScheme.Suggestions.FIELDS_DISPLAY_MODE
                to model.view.getDisplayMode().toString(),
        LocalDataStoreScheme.Suggestions.SORT
                to model.sort.javaClass.simpleName,
        LocalDataStoreScheme.Suggestions.SORT_BY_ASCENDING
                to model.sort.isByAscending().toString(),
        LocalDataStoreScheme.Suggestions.ADDING_MODE
                to model.addingMode.toString(),
        LocalDataStoreScheme.Suggestions.DISPLAY_NAMES
                to model.displaySuggestionNames.toString(),
        LocalDataStoreScheme.Suggestions.DISPLAY_DETAILS
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