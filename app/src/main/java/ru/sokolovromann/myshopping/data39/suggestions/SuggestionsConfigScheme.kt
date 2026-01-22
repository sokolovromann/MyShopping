package ru.sokolovromann.myshopping.data39.suggestions

import androidx.datastore.preferences.core.stringPreferencesKey

object SuggestionsConfigScheme {
    const val DATA_STORE_NAME: String = "local_suggestions_config"

    val PRE_INSTALLED = stringPreferencesKey("pre_installed")
    val VIEW_MODE = stringPreferencesKey("view_mode")
    val SORT = stringPreferencesKey("sort")
    val SORT_ORDER = stringPreferencesKey("sort_order")
    val ADD = stringPreferencesKey("add")
    val TAKE_NAMES = stringPreferencesKey("take_names")
    val TAKE_DESCRIPTIONS = stringPreferencesKey("take_descriptions")
    val TAKE_QUANTITIES = stringPreferencesKey("take_quantities")
    val TAKE_MONEY = stringPreferencesKey("take_money")
}