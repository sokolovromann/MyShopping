package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object LocalDataStoreScheme {

    object General {
        val THEME = stringPreferencesKey("theme")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val DATE_TIME_FORMATTING_MODE = stringPreferencesKey("date_time_formatting_mode")
        val IS_24_HOUR_TIME_FORMAT = stringPreferencesKey("is_24_hour_time_format")
        val MONEY_FORMATTING_MODE= stringPreferencesKey("money_formatting_mode")
        val CURRENCY = stringPreferencesKey("currency")
        val CURRENCY_DISPLAY_SIDE = stringPreferencesKey("currency_display_side")
        val KEYBOARD_DISPLAY_DELAY = stringPreferencesKey("keyboard_display_delay")
    }

    object Carts {
        val VIEW = stringPreferencesKey("view")
        val PRODUCTS_DISPLAY_MODE = stringPreferencesKey("products_display_mode")
        val SORT = stringPreferencesKey("sort")
        val SORT_BY_ASCENDING = stringPreferencesKey("sort_by_ascending")
        val GROUP_BY_STATUS = stringPreferencesKey("group_by_status")
        val DISPLAY_EMPTY = stringPreferencesKey("display_empty")
        val CALCULATE_PRODUCTS_TOTAL = stringPreferencesKey("calculate_products_total")
        val PRODUCTS_TOTAL_CALCULATING_MODE = stringPreferencesKey("products_total_calculating_mode")
        val AFTER_ADDING = stringPreferencesKey("after_adding")
        val AFTER_COMPLETING = stringPreferencesKey("after_completing")
        val AFTER_ARCHIVING = stringPreferencesKey("after_archiving")
        val AFTER_TAPPING_BY_CHECKBOX = stringPreferencesKey("after_tapping_by_checkbox")
        val CHECKBOX_COLOR = stringPreferencesKey("checkbox_color")
        val SWIPE_LEFT = stringPreferencesKey("swipe_left")
        val SWIPE_RIGHT = stringPreferencesKey("swipe_right")
        val DELETION_FROM_TRASH = stringPreferencesKey("deletion_from_trash")
    }

    object Products {
        val VIEW = stringPreferencesKey("view")
        val SORT = stringPreferencesKey("sort")
        val SORT_BY_ASCENDING = stringPreferencesKey("sort_by_ascending")
        val GROUP_BY_STATUS = stringPreferencesKey("group_by_status")
        val ADDING_MODE = stringPreferencesKey("adding_mode")
        val CALCULATE_PRODUCTS_TOTAL = stringPreferencesKey("calculate_products_total")
        val PRODUCTS_TOTAL_CALCULATING_MODE = stringPreferencesKey("products_total_calculating_mode")
        val STRIKETHROUGH_COMPLETED = stringPreferencesKey("strikethrough_completed")
        val AFTER_COMPLETING = stringPreferencesKey("after_completing")
        val AFTER_TAPPING_BY_CHECKBOX = stringPreferencesKey("after_tapping_by_checkbox")
        val CHECKBOX_COLOR = stringPreferencesKey("checkbox_color")
        val AFTER_TAPPING_BY_ITEM = stringPreferencesKey("after_tapping_by_item")
        val SWIPE_LEFT = stringPreferencesKey("swipe_left")
        val SWIPE_RIGHT = stringPreferencesKey("swipe_right")
    }

    object ProductsWidget {
        val THEME = stringPreferencesKey("theme")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val SORT = stringPreferencesKey("sort")
        val SORT_BY_ASCENDING = stringPreferencesKey("sort_by_ascending")
        val GROUP_BY_STATUS = stringPreferencesKey("group_by_status")
    }

    object AddEditProduct {
        val LOCK_FIELD = stringPreferencesKey("lock_field")
        val AFTER_TAPPING_BY_ENTER = stringPreferencesKey("after_tapping_by_enter")
        val AFTER_ADDING = stringPreferencesKey("after_adding")
        val AFTER_EDITING = stringPreferencesKey("after_editing")
        val TAX = stringPreferencesKey("tax")
    }

    object Suggestions {
        val VIEW = stringPreferencesKey("view")
        val FIELDS_DISPLAY_MODE = stringPreferencesKey("fields_display_mode")
        val SORT = stringPreferencesKey("sort")
        val SORT_BY_ASCENDING = stringPreferencesKey("sort_by_ascending")
        val ADDING_MODE = stringPreferencesKey("adding_mode")
        val DISPLAY_NAMES = stringPreferencesKey("display_names")
        val DISPLAY_DETAILS = stringPreferencesKey("display_details")
    }

    object Backup {
        val DIRECTORY = stringPreferencesKey("directory")
    }

    object User {
        val API = stringPreferencesKey("api")
    }
}