package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class MoveProductState {

    private var product by mutableStateOf(Product())

    private var shoppingLists: List<ShoppingList> by mutableStateOf(listOf())

    var screenData by mutableStateOf(MoveProductScreenData())
        private set

    fun showLoading() {
        screenData = MoveProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: ShoppingListPreferences, location: ShoppingListLocation) {
        shoppingLists = listOf()
        screenData = MoveProductScreenData(
            screenState = ScreenState.Nothing,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists, location: ShoppingListLocation) {
        this.shoppingLists = shoppingLists.formatShoppingLists()
        val preferences = shoppingLists.preferences

        screenData = MoveProductScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            multiColumns = preferences.multiColumns,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun saveProduct(product: Product) {
        this.product = product
    }

    fun selectShoppingList(uid: String) {
        screenData = screenData.copy(shoppingListSelectedUid = uid)
    }

    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }

    fun getProductResult(): Result<Product> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.shoppingListSelectedUid == null) {
            screenData = screenData.copy(screenState = ScreenState.Showing)
            Result.failure(Exception())
        } else {
            val shoppingUid = screenData.shoppingListSelectedUid!!
            val position = shoppingLists.find { it.uid == shoppingUid }?.nextProductsPosition() ?: 0
            val success = product.copy(
                position = position,
                shoppingUid = screenData.shoppingListSelectedUid!!,
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }
}

data class MoveProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val shoppingListSelectedUid: String? = null,
    val multiColumns: Boolean = false,
    val location: ShoppingListLocation = ShoppingListLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)