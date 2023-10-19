package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.AppConfig

@Deprecated("Use /model/ShoppingListWithConfig")
data class CalculateChange(
    private val shoppingList: ShoppingList? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _shoppingList = shoppingList ?: ShoppingList()
    private val userPreferences = appConfig.userPreferences

    fun getDisplayTotal(): String {
        return _shoppingList.calculateTotal().getDisplayValue()
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun calculateChange(userMoney: Float?): String {
        val total = _shoppingList.calculateTotal().value
        return if (userMoney == null || userMoney <= total) {
            ""
        } else {
            val value = userMoney - total
            Money(
                value = value,
                currency = userPreferences.currency,
                decimalFormat = userPreferences.moneyDecimalFormat
            ).getDisplayValue()
        }
    }
}