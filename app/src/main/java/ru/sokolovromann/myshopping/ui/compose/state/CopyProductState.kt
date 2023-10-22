package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

class CopyProductState {

    private var products by mutableStateOf<List<Product>>(listOf())

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(CopyProductScreenData())
        private set

    fun showLoading() {
        screenData = CopyProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingLists: ShoppingLists, location: ShoppingListLocation) {
        this.shoppingLists = shoppingLists

        screenData = CopyProductScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            location = location,
            fontSize = shoppingLists.getFontSize()
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists, location: ShoppingListLocation) {
        this.shoppingLists = shoppingLists

        val pinnedShoppingLists = if (location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getActivePinnedShoppingListItems()
        } else {
            listOf()
        }

        val otherShoppingLists = if (location == ShoppingListLocation.PURCHASES) {
            shoppingLists.getOtherShoppingListItems()
        } else {
            shoppingLists.getAllShoppingListItems()
        }

        screenData = CopyProductScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = pinnedShoppingLists,
            otherShoppingLists = otherShoppingLists,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            multiColumns = shoppingLists.isMultiColumns(),
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            location = location,
            fontSize = shoppingLists.getFontSize(),
            showHiddenShoppingLists = shoppingLists.displayHiddenShoppingLists()
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
            shoppingLists.getAllShoppingListItems()
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

        return shoppingLists.copyProducts(
            shoppingUid = screenData.shoppingListSelectedUid,
            products = products
        )
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        return shoppingLists.createShoppingList()
    }
}

data class CopyProductScreenData(
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