package ru.sokolovromann.myshopping.core.data.datasource

import androidx.datastore.preferences.core.stringPreferencesKey

object AddEditProductPreferencesScheme {

    const val FILE_NAME = "add_edit_product_preferences"
    val LOCK_FIELD_KEY = stringPreferencesKey("lock_field")
    val AFTER_TAPPING_BY_ENTER_KEY = stringPreferencesKey("after_tapping_by_enter")
    val AFTER_ADDING_KEY = stringPreferencesKey("after_adding")
    val AFTER_EDITING_KEY = stringPreferencesKey("after_editing")
    val TAX_KEY = stringPreferencesKey("tax")
}