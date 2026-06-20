package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object SuggestionsPreferencesScheme {

    const val FILE_NAME = "suggestions_preferences"
    val VIEW_KEY = stringPreferencesKey("view")
    val FIELDS_DISPLAY_MODE_KEY = stringPreferencesKey("fields_display_mode")
    val SORT_KEY = stringPreferencesKey("sort")
    val SORT_BY_ASCENDING_KEY = stringPreferencesKey("sort_by_ascending")
    val ADDING_MODE_KEY = stringPreferencesKey("adding_mode")
    val DISPLAY_NAMES_KEY = stringPreferencesKey("display_names")
    val DISPLAY_DETAILS_KEY = stringPreferencesKey("display_details")
}