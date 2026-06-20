package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object CartsPreferencesScheme {

    const val FILE_NAME = "carts_preferences"
    val VIEW_KEY = stringPreferencesKey("view")
    val PRODUCTS_DISPLAY_MODE_KEY = stringPreferencesKey("products_display_mode")
    val SORT_KEY = stringPreferencesKey("sort")
    val SORT_BY_ASCENDING_KEY = stringPreferencesKey("sort_by_ascending")
    val GROUP_BY_STATUS_KEY = stringPreferencesKey("group_by_status")
    val DISPLAY_EMPTY_KEY = stringPreferencesKey("display_empty")
    val CALCULATE_PRODUCTS_TOTAL_KEY = stringPreferencesKey("calculate_products_total")
    val PRODUCTS_TOTAL_CALCULATING_MODE_KEY = stringPreferencesKey("products_total_calculating_mode")
    val AFTER_ADDING_KEY = stringPreferencesKey("after_adding")
    val AFTER_COMPLETING_KEY = stringPreferencesKey("after_completing")
    val AFTER_ARCHIVING_KEY = stringPreferencesKey("after_archiving")
    val AFTER_TAPPING_BY_CHECKBOX_KEY = stringPreferencesKey("after_tapping_by_checkbox")
    val CHECKBOX_COLOR_KEY = stringPreferencesKey("checkbox_color")
    val SWIPE_LEFT_KEY = stringPreferencesKey("swipe_left")
    val SWIPE_RIGHT_KEY = stringPreferencesKey("swipe_right")
    val DELETION_FROM_TRASH_KEY = stringPreferencesKey("deletion_from_trash")
}