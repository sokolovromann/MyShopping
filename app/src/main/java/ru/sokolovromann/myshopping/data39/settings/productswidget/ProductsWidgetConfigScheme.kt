package ru.sokolovromann.myshopping.data39.settings.productswidget

import androidx.datastore.preferences.core.stringPreferencesKey

object ProductsWidgetConfigScheme {
    const val DATA_STORE_NAME: String = "local_products_widget_config"

    val THEME = stringPreferencesKey("theme")
    val FONT_SIZE = stringPreferencesKey("font_size")
    val SORT = stringPreferencesKey("sort")
    val SORT_PARAMS = stringPreferencesKey("sort_params")
    val GROUP = stringPreferencesKey("group")
}