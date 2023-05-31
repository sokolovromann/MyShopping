package ru.sokolovromann.myshopping.data.repository.model

data class EditShoppingListTotal(
    val shoppingList: ShoppingList? = null,
    val preferences: AppPreferences = AppPreferences()
) {

    fun total(): Money {
        return shoppingList?.total ?: Money()
    }

    fun totalFormatted(): Boolean {
        return shoppingList?.totalFormatted ?: false
    }
}