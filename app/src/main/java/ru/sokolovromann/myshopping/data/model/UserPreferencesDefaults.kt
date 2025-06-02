package ru.sokolovromann.myshopping.data.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object UserPreferencesDefaults {

    const val MULTI_COLUMNS = false
    const val STRIKETHROUGH_COMPLETED_PRODUCTS = false
    const val SORT_FORMATTED = false
    const val DISPLAY_OTHER_FIELDS = false
    const val DISPLAY_LONG_TOTAL = false
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
    const val AUTOMATICALLY_EMPTY_TRASH = false
    const val LIST_OF_AUTOCOMPLETES = false
    const val DISPLAY_EMPTY_SHOPPINGS = true
    const val ARCHIVE_AS_COMPLETED = false

    val NIGHT_THEME = NightTheme.DISABLED
    val FONT_SIZE = FontSize.DefaultValue
    val SORT = Sort()
    val DISPLAY_COMPLETED = DisplayCompleted.DefaultValue
    val DISPLAY_TOTAL = DisplayTotal.DefaultValue
    val DISPLAY_SHOPPINGS_PRODUCTS = DisplayProducts.DefaultValue
    val LOCK_PRODUCT_ELEMENT = LockProductElement.DefaultValue
    val AFTER_SAVE_PRODUCT = AfterSaveProduct.DefaultValue
    val AFTER_PRODUCT_COMPLETED = AfterProductCompleted.DefaultValue
    val AFTER_ADD_SHOPPING = AfterAddShopping.DefaultValue
    val AFTER_SHOPPING_COMPLETED = AfterShoppingCompleted.DefaultValue
    val SWIPE_PRODUCT = SwipeProduct.DefaultValue
    val SWIPE_SHOPPING = SwipeShopping.DefaultValue

    fun getCurrency(): Currency {
        return Currency(
            symbol = "",
            displayToLeft = false
        )
    }

    fun getTaxRate(): Money {
        return Money(
            value = BigDecimal.ZERO,
            currency = getCurrency(),
            asPercent = true,
            decimalFormat = getMoneyDecimalFormat()
        )
    }

    fun getMoneyDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }
    }

    fun getQuantityDecimalFormat(): DecimalFormat {
        return DecimalFormat().apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 3
            roundingMode = RoundingMode.HALF_UP
        }
    }
}