package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class MoveProductState {

    private var products by mutableStateOf<List<Product>>(listOf())

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(MoveProductScreenData())
        private set

    fun showLoading() {
        screenData = MoveProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences, location: ShoppingListLocation) {
        shoppingLists = ShoppingLists(preferences = preferences)
        screenData = MoveProductScreenData(
            screenState = ScreenState.Nothing,
            productsOneLine = preferences.shoppingsProductsOneLine,
            smartphoneScreen = preferences.smartphoneScreen,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists, location: ShoppingListLocation) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.preferences

        val showHiddenShoppingLists = preferences.displayCompletedPurchases == DisplayCompleted.HIDE
                && shoppingLists.hasHiddenShoppingLists()

        screenData = MoveProductScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            productsOneLine = preferences.shoppingsProductsOneLine,
            multiColumns = preferences.shoppingsMultiColumns,
            smartphoneScreen = preferences.smartphoneScreen,
            location = location,
            fontSize = preferences.fontSize,
            showHiddenShoppingLists = showHiddenShoppingLists
        )
    }

    fun saveProducts(products: List<Product>) {
        this.products = products
    }

    fun selectShoppingList(uid: String) {
        screenData = screenData.copy(shoppingListSelectedUid = uid)
    }

    fun displayHiddenShoppingLists() {
        screenData = screenData.copy(
            shoppingLists = shoppingLists.getShoppingListItems(DisplayCompleted.LAST),
            showHiddenShoppingLists = false
        )
    }

    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }

    fun getProductsResult(): Result<List<Product>> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.shoppingListSelectedUid == null) {
            screenData = screenData.copy(screenState = ScreenState.Showing)
            Result.failure(Exception())
        } else {
            val shoppingUid = screenData.shoppingListSelectedUid!!
            val position = shoppingLists.formatShoppingLists()
                .find { it.uid == shoppingUid }?.nextProductsPosition() ?: 0
            val success = products.mapIndexed { index, product ->
                val newPosition = position + index
                product.copy(
                    position = newPosition,
                    shoppingUid = screenData.shoppingListSelectedUid!!,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }
}

data class MoveProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val productsOneLine: Boolean = false,
    val shoppingListSelectedUid: String? = null,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val location: ShoppingListLocation = ShoppingListLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false
)