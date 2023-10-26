package ru.sokolovromann.myshopping.data.model

data class ShoppingListsWithConfig(
    val shoppingLists: List<ShoppingList> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    fun getTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += it.shopping.total.value
        }

        return Money(
            value = total,
            currency = appConfig.userPreferences.currency,
            asPercent = false,
            decimalFormat = appConfig.userPreferences.moneyDecimalFormat
        )
    }

    fun isEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }
}