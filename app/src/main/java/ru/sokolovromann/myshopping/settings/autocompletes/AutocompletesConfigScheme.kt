package ru.sokolovromann.myshopping.settings.autocompletes

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AutocompletesConfigScheme {
    const val DATA_STORE_NAME: String = "local_autocompletes_config"

    val VIEW_MODE = stringPreferencesKey("view_mode")
    val VIEW_MODE_PARAMS = stringPreferencesKey("view_mode_params")
    val SORT = stringPreferencesKey("sort")
    val SORT_PARAMS = stringPreferencesKey("sort_params")
    val MAXIMUM_NAMES_NUMBER = intPreferencesKey("maximum_names_number")
    val MAXIMUM_IMAGES_NUMBER = intPreferencesKey("maximum_images_number")
    val MAXIMUM_MANUFACTURERS_NUMBER = intPreferencesKey("maximum_manufacturers_number")
    val MAXIMUM_BRANDS_NUMBER = intPreferencesKey("maximum_brands_number")
    val MAXIMUM_SIZES_NUMBER = intPreferencesKey("maximum_sizes_number")
    val MAXIMUM_COLORS_NUMBER = intPreferencesKey("maximum_colors_number")
    val MAXIMUM_QUANTITIES_NUMBER = intPreferencesKey("maximum_quantities_number")
    val MAXIMUM_PRICES_NUMBER = intPreferencesKey("maximum_prices_number")
    val MAXIMUM_DISCOUNTS_NUMBER = intPreferencesKey("maximum_discounts_number")
    val MAXIMUM_TAX_RATES_NUMBER = intPreferencesKey("maximum_tax_rates_number")
    val MAXIMUM_COSTS_NUMBER = intPreferencesKey("maximum_costs_number")
}