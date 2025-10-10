package ru.sokolovromann.myshopping.data39.settings.addeditproduct

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AddEditProductConfigScheme {
    const val DATA_STORE_NAME: String = "local_add_edit_product_config"

    val DISPLAY_NAME = booleanPreferencesKey("display_name")
    val DISPLAY_IMAGE = booleanPreferencesKey("display_image")
    val DISPLAY_MANUFACTURER = booleanPreferencesKey("display_manufacturer")
    val DISPLAY_BRAND = booleanPreferencesKey("display_brand")
    val DISPLAY_SIZE = booleanPreferencesKey("display_size")
    val DISPLAY_COLOR = booleanPreferencesKey("display_color")
    val DISPLAY_QUANTITY = booleanPreferencesKey("display_quantity")
    val DISPLAY_PLUS_MINUS_ONE_QUANTITY = booleanPreferencesKey("display_plus_minus_one_quantity")
    val DISPLAY_PRICE = booleanPreferencesKey("display_price")
    val DISPLAY_DISCOUNT = booleanPreferencesKey("display_discount")
    val DISPLAY_TAX_RATE = booleanPreferencesKey("display_taxRate")
    val DISPLAY_COST = booleanPreferencesKey("display_cost")
    val DISPLAY_NOTE = booleanPreferencesKey("display_note")
    val DISPLAY_ID = booleanPreferencesKey("display_id")
    val DISPLAY_CREATED = booleanPreferencesKey("display_created")
    val DISPLAY_LAST_MODIFIED = booleanPreferencesKey("display_last_modified")
    val LOCK_FIELD = stringPreferencesKey("lock_field")
    val KEYBOARD_DISPLAY_DELAY = stringPreferencesKey("keyboard_display_delay")
    val AFTER_TAPPING_BY_ENTER = stringPreferencesKey("after_tapping_by_enter")
    val AFTER_ADDING = stringPreferencesKey("after_adding")
    val AFTER_EDITING = stringPreferencesKey("after_editing")
}
