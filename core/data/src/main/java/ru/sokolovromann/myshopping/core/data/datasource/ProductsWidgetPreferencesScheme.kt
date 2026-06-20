package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object ProductsWidgetPreferencesScheme {

    const val FILE_NAME = "products_widget_preferences"
    val THEME_KEY = stringPreferencesKey("theme")
    val FONT_SIZE_KEY = stringPreferencesKey("font_size")
    val SORT_KEY = stringPreferencesKey("sort")
    val SORT_BY_ASCENDING_KEY = stringPreferencesKey("sort_by_ascending")
    val GROUP_BY_STATUS_KEY = stringPreferencesKey("group_by_status")
}