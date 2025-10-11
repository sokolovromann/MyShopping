package ru.sokolovromann.myshopping.data39.settings.carts

import androidx.datastore.preferences.core.stringPreferencesKey

object CartsConfigScheme {
    const val DATA_STORE_NAME: String = "local_carts_config"

    val VIEW_MODE = stringPreferencesKey("view_mode")
    val VIEW_MODE_PARAMS = stringPreferencesKey("view_mode_params")
    val SORT = stringPreferencesKey("sort")
    val SORT_PARAMS = stringPreferencesKey("sort_params")
    val GROUP = stringPreferencesKey("group")
    val CALCULATE_TOTAL = stringPreferencesKey("calculate_total")
    val AFTER_ADDING = stringPreferencesKey("after_adding")
    val AFTER_COMPLETING = stringPreferencesKey("after_completing")
    val AFTER_ARCHIVING = stringPreferencesKey("after_archiving")
    val AFTER_TAPPING_BY_CHECKBOX = stringPreferencesKey("after_tapping_by_checkbox")
    val CHECKBOXES_COLOR = stringPreferencesKey("checkboxes_color")
    val SWIPE_LEFT = stringPreferencesKey("swipe_left")
    val SWIPE_RIGHT = stringPreferencesKey("swipe_right")
    val EMPTY_CARTS = stringPreferencesKey("empty_carts")
    val DELETION_FROM_TRASH = stringPreferencesKey("deletion_from_trash")
}