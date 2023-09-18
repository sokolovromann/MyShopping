package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidMoneyException

data class EditShoppingListTotal(
    private val shoppingList: ShoppingList? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _shoppingList = shoppingList ?: ShoppingList()
    private val userPreferences = appConfig.userPreferences

    fun createShoppingList(total: Float?): Result<ShoppingList> {
        return if (total == null) {
            val exception = InvalidMoneyException("Money must not be null")
            Result.failure(exception)
        } else {
            val moneyTotal = _shoppingList.total.copy(value = total)
            val success = _shoppingList.copy(
                total = moneyTotal,
                totalFormatted = moneyTotal.isNotEmpty(),
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }

    fun getFieldTotal(): String {
        val total = _shoppingList.total
        return if (total.isEmpty()) "" else total.getFormattedValueWithoutSeparators()
    }

    fun isTotalFormatted(): Boolean {
        return _shoppingList.totalFormatted
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }
}
