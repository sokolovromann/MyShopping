package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

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
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompletedPurchases,
            coloredCheckbox = preferences.coloredCheckbox,
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

        val pinnedShoppingLists = if (location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getActivePinnedShoppingListItems()
        } else {
            listOf()
        }

        val otherShoppingLists = if (location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getOtherShoppingListItems()
        } else {
            shoppingLists.getAllShoppingListItems(splitByPinned = false)
        }

        screenData = MoveProductScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = pinnedShoppingLists,
            otherShoppingLists = otherShoppingLists,
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompletedPurchases,
            coloredCheckbox = preferences.coloredCheckbox,
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
        val pinnedShoppingLists = if (screenData.location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getActivePinnedShoppingListItems()
        } else {
            listOf()
        }

        val otherShoppingLists = if (screenData.location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getOtherShoppingListItems()
        } else {
            shoppingLists.getAllShoppingListItems(false, DisplayCompleted.LAST)
        }

        screenData = screenData.copy(
            pinnedShoppingLists = pinnedShoppingLists,
            otherShoppingLists = otherShoppingLists,
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
            val position = shoppingLists.getAllShoppingLists(splitByPinned = false)
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

    fun getShoppingListResult(): Result<ShoppingList> {
        return if (shoppingLists.shoppingListsLastPosition == null) {
            Result.failure(Exception())
        } else {
            val position = shoppingLists.shoppingListsLastPosition?.plus(1) ?: 0
            val success = ShoppingList(position = position)
            Result.success(success)
        }
    }
}

data class MoveProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val shoppingListSelectedUid: String? = null,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val location: ShoppingListLocation = ShoppingListLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false
)