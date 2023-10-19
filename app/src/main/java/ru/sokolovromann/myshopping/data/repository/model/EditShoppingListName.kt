package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.model.AppConfig

@Deprecated("Use ShoppingListWithConfig")
data class EditShoppingListName(
    private val shoppingList: ShoppingList? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _shoppingList = shoppingList ?: ShoppingList()
    private val userPreferences = appConfig.userPreferences

    fun createShoppingList(name: String?): Result<ShoppingList> {
        return if (name == null) {
            val exception = InvalidNameException("Name must not be null")
            Result.failure(exception)
        } else {
            val success = _shoppingList.copy(
                name = name.trim(),
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }

    fun getFieldName(): String {
        return _shoppingList.name
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun hasName(): Boolean {
        return _shoppingList.name.isNotEmpty()
    }
}