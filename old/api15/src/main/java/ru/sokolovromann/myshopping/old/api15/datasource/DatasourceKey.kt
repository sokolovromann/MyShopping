package ru.sokolovromann.myshopping.old.api15.datasource

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DatasourceKey {
    object Build {
        val appFirstTime = stringPreferencesKey("app_first_time")
        val userCodeVersion = intPreferencesKey("user_code_version")
    }

    object User {
        val appNightTheme = booleanPreferencesKey("night_theme")
        val widgetNightTheme = booleanPreferencesKey("widget_night_theme")
        val appFontSize = stringPreferencesKey("font_size")
        val widgetFontSize = stringPreferencesKey("widget_font_size")
        val displayMoney = booleanPreferencesKey("display_money")
        val currency = stringPreferencesKey("currency")
        val displayCurrencyToLeft= booleanPreferencesKey("display_currency_to_left")
        val taxRate = floatPreferencesKey("tax_rate")
        val taxRateAsPercent = booleanPreferencesKey("tax_rate_as_percent")
        val shoppingsMultiColumns = booleanPreferencesKey("shoppings_multi_columns")
        val shoppingsSortBy = stringPreferencesKey("shoppings_sort_by")
        val shoppingsSortAscending = booleanPreferencesKey("shoppings_sort_ascending")
        val shoppingsSortFormatted = booleanPreferencesKey("shoppings_sort_formatted")
        val productsMultiColumns = booleanPreferencesKey("products_multi_columns")
        val displayCompleted = stringPreferencesKey("display_completed_purchases")
        val widgetDisplayCompleted = stringPreferencesKey("widget_display_completed_purchases")
        val strikethroughCompletedProducts = booleanPreferencesKey("strikethrough_completed_products")
        val displayTotal = stringPreferencesKey("display_purchases_total")
        val displayLongTotal = booleanPreferencesKey("display_long_purchases_total")
        val displayOtherFields = booleanPreferencesKey("display_purchases_other_fields")
        val coloredCheckbox = booleanPreferencesKey("highlight_checkbox")
        val displayShoppingsProducts = stringPreferencesKey("display_shoppings_products")
        val purchasesSeparator = stringPreferencesKey("purchases_separator")
        val editProductAfterCompleted = booleanPreferencesKey("edit_product_after_completed")
        val lockProductElement = stringPreferencesKey("lock_product_element")
        val completedWithCheckbox = booleanPreferencesKey("completed_with_checkbox")
        val enterToSaveProduct = booleanPreferencesKey("enter_to_save_product")
        val displayDefaultAutocompletes = booleanPreferencesKey("display_default_autocompletes")
        val maxAutocompletesNames = intPreferencesKey("max_autocomplete_names")
        val maxAutocompletesQuantities = intPreferencesKey("max_autocomplete_quantities")
        val maxAutocompletesMoneys = intPreferencesKey("max_autocomplete_moneys")
        val maxAutocompletesOthers = intPreferencesKey("max_autocomplete_others")
        val saveProductToAutocompletes = booleanPreferencesKey("save_product_to_autocompletes")
        val minMoneyFractionDigits = intPreferencesKey("min_money_fraction_digits")
        val minQuantityFractionDigits = intPreferencesKey("min_quantity_fraction_digits")
        val maxMoneyFractionDigits = intPreferencesKey("max_money_fraction_digits")
        val maxQuantityFractionDigits = intPreferencesKey("max_quantity_fraction_digits")
        val automaticallyEmptyTrash = booleanPreferencesKey("automatically_empty_trash")
        val displayListOfAutocompletes = booleanPreferencesKey("display_list_of_autocompletes")
        val afterSaveProduct = stringPreferencesKey("after_save_product")
        val afterProductCompleted = stringPreferencesKey("after_product_completed")
        val afterAddShopping = stringPreferencesKey("after_add_shopping")
        val afterShoppingCompleted = stringPreferencesKey("after_shopping_completed")
        val displayEmptyShoppings = booleanPreferencesKey("display_empty_shoppings")
        val swipeProductLeft = stringPreferencesKey("swipe_product_left")
        val swipeProductRight = stringPreferencesKey("swipe_product_right")
        val swipeShoppingLeft = stringPreferencesKey("swipe_shopping_left")
        val swipeShoppingRight = stringPreferencesKey("swipe_shopping_right")
        val archiveAsCompleted = booleanPreferencesKey("archive_as_completed")
    }
}