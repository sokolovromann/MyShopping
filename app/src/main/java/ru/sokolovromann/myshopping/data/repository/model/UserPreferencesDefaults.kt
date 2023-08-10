package ru.sokolovromann.myshopping.data.repository.model

import java.math.RoundingMode
import java.text.DecimalFormat

object UserPreferencesDefaults {

    const val NIGHT_THEME = false
    const val MULTI_COLUMNS = false
    const val DISPLAY_OTHER_FIELDS = true
    const val COLORED_CHECKBOX = true
    const val PURCHASES_SEPARATOR = " • "
    const val EDIT_PRODUCT_AFTER_COMPLETED = false
    const val COMPLETED_WITH_CHECKBOX = true
    const val ENTER_TO_SAVE_PRODUCTS = true
    const val DISPLAY_DEFAULT_AUTOCOMPLETES = true
    const val MAX_AUTOCOMPLETES_NAMES = 10
    const val MAX_AUTOCOMPLETES_QUANTITIES = 5
    const val MAX_AUTOCOMPLETES_MONEYS = 3
    const val MAX_AUTOCOMPLETES_OTHERS = 3
    const val SAVE_PRODUCT_TO_AUTOCOMPLETES = true
    const val DISPLAY_MONEY = true

    val FONT_SIZE = FontSize.DefaultValue
    val DISPLAY_COMPLETED = DisplayCompleted.DefaultValue
    val DISPLAY_TOTAL = DisplayTotal.DefaultValue
    val DISPLAY_SHOPPINGS_PRODUCTS = DisplayProducts.DefaultValue
    val LOCK_PRODUCT_ELEMENT = LockProductElement.DefaultValue
    val CURRENCY = Currency(symbol = "", displayToLeft = false)
    val MONEY_DECIMAL_FORMAT = DecimalFormat().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        roundingMode = RoundingMode.HALF_UP
    }
    val QUANTITY_DECIMAL_FORMAT = DecimalFormat().apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 3
        roundingMode = RoundingMode.HALF_UP
    }
    val TAX_RATE = AppFloat(
        value = 0f,
        type = AppFloatType.Percent,
        decimalFormat = MONEY_DECIMAL_FORMAT
    )
}