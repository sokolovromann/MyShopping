package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class CopyProductState {

    private var products by mutableStateOf<List<Product>>(listOf())

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(CopyProductScreenData())
        private set

    fun showLoading() {
        screenData = CopyProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences, location: ShoppingListLocation) {
        shoppingLists = ShoppingLists(preferences = preferences)
        screenData = CopyProductScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
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

        screenData = CopyProductScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
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
                Product(
                    position = newPosition,
                    shoppingUid = shoppingUid,
                    name = product.name,
                    quantity = product.quantity,
                    price = product.price,
                    discount = product.discount,
                    taxRate = product.taxRate,
                    completed = product.completed
                )
            }

            Result.success(success)
        }
    }
}

data class CopyProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val highlightCheckbox: Boolean = false,
    val shoppingListSelectedUid: String? = null,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val location: ShoppingListLocation = ShoppingListLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false
)