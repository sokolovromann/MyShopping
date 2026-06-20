package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object ProductsPreferencesScheme {

    const val FILE_NAME = "products_preferences"
    val VIEW_KEY = stringPreferencesKey("view")
    val SORT_KEY = stringPreferencesKey("sort")
    val SORT_BY_ASCENDING_KEY = stringPreferencesKey("sort_by_ascending")
    val GROUP_BY_STATUS_KEY = stringPreferencesKey("group_by_status")
    val ADDING_MODE_KEY = stringPreferencesKey("adding_mode")
    val CALCULATE_PRODUCTS_TOTAL_KEY = stringPreferencesKey("calculate_products_total")
    val PRODUCTS_TOTAL_CALCULATING_MODE_KEY = stringPreferencesKey("products_total_calculating_mode")
    val STRIKETHROUGH_COMPLETED_KEY = stringPreferencesKey("strikethrough_completed")
    val AFTER_COMPLETING_KEY = stringPreferencesKey("after_completing")
    val AFTER_TAPPING_BY_CHECKBOX_KEY = stringPreferencesKey("after_tapping_by_checkbox")
    val CHECKBOX_COLOR_KEY = stringPreferencesKey("checkbox_color")
    val AFTER_TAPPING_BY_ITEM_KEY = stringPreferencesKey("after_tapping_by_item")
    val SWIPE_LEFT_KEY = stringPreferencesKey("swipe_left")
    val SWIPE_RIGHT_KEY = stringPreferencesKey("swipe_right")
}