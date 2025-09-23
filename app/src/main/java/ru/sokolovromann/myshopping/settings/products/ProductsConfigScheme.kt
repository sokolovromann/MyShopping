package ru.sokolovromann.myshopping.settings.products

import androidx.datastore.preferences.core.stringPreferencesKey

object ProductsConfigScheme {
    const val DATA_STORE_NAME: String = "local_products_config"

    val VIEW_MODE = stringPreferencesKey("view_mode")
    val SORT = stringPreferencesKey("sort")
    val SORT_PARAMS = stringPreferencesKey("sort_params")
    val GROUP = stringPreferencesKey("group")
    val ADDING_MODE = stringPreferencesKey("adding_mode")
    val CALCULATE_TOTAL = stringPreferencesKey("calculate_total")
    val CALCULATE_TOTAL_PARAMS = stringPreferencesKey("calculate_total_params")
    val STRIKETHROUGH_COMPLETED = stringPreferencesKey("strikethrough_completed")
    val AFTER_COMPETING = stringPreferencesKey("after_completing")
    val AFTER_TAPPING_BY_CHECKBOX = stringPreferencesKey("after_tapping_by_checkbox")
    val CHECKBOXES_COLOR = stringPreferencesKey("checkboxes_color")
    val AFTER_TAPPING_BY_ITEM = stringPreferencesKey("after_tapping_by_item")
    val SWIPE_LEFT = stringPreferencesKey("swipe_left")
    val SWIPE_RIGHT = stringPreferencesKey("swipe_right")
}