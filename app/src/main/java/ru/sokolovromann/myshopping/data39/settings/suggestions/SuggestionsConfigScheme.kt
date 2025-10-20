package ru.sokolovromann.myshopping.data39.settings.suggestions

import androidx.datastore.preferences.core.stringPreferencesKey

object SuggestionsConfigScheme {
    const val DATA_STORE_NAME: String = "local_suggestions_config"

    val VIEW_MODE = stringPreferencesKey("view_mode")
    val SORT = stringPreferencesKey("sort")
    val SORT_PARAMS = stringPreferencesKey("sort_params")
    val TAKE_NAMES = stringPreferencesKey("take_names")
    val TAKE_IMAGES = stringPreferencesKey("take_images")
    val TAKE_MANUFACTURERS = stringPreferencesKey("take_manufacturers")
    val TAKE_BRANDS = stringPreferencesKey("take_brands")
    val TAKE_SIZES = stringPreferencesKey("take_sizes")
    val TAKE_COLORS = stringPreferencesKey("take_colors")
    val TAKE_QUANTITIES = stringPreferencesKey("take_quantities")
    val TAKE_UNIT_PRICES = stringPreferencesKey("take_unit_prices")
    val TAKE_DISCOUNTS = stringPreferencesKey("take_discounts")
    val TAKE_TAX_RATES = stringPreferencesKey("take_tax_rates")
    val TAKE_COSTS = stringPreferencesKey("take_costs")
}