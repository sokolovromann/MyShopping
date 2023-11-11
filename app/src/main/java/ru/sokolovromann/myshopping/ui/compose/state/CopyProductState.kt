package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

class CopyProductState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var products by mutableStateOf<List<Product>>(listOf())
        private set

    var screenData by mutableStateOf(CopyProductScreenData())
        private set

    fun showLoading() {
        screenData = CopyProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingListsWithConfig: ShoppingListsWithConfig, location: ShoppingLocation) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        screenData = CopyProductScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            location = location,
            fontSize = userPreferences.fontSize
        )
    }

    fun showShoppingLists(shoppingListsWithConfig: ShoppingListsWithConfig, location: ShoppingLocation) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        val pinnedShoppingLists = if (location == ShoppingLocation.PURCHASES) {
            shoppingListsWithConfig.getActivePinnedShoppingListItems()
        } else {
            listOf()
        }

        val otherShoppingLists = if (location == ShoppingLocation.PURCHASES) {
            shoppingListsWithConfig.getOtherShoppingListItems()
        } else {
            shoppingListsWithConfig.getAllShoppingListItems()
        }

        screenData = CopyProductScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = pinnedShoppingLists,
            otherShoppingLists = otherShoppingLists,
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            multiColumns = userPreferences.shoppingsMultiColumns,
            smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            location = location,
            fontSize = userPreferences.fontSize,
            showHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        )
    }

    fun saveProducts(products: List<Product>) {
        this.products = products
    }

    fun displayHiddenShoppingLists() {
        val pinnedShoppingLists = if (screenData.location == ShoppingLocation.PURCHASES) {
            shoppingListsWithConfig.getActivePinnedShoppingListItems()
        } else {
            listOf()
        }

        val otherShoppingLists = if (screenData.location == ShoppingLocation.PURCHASES) {
            shoppingListsWithConfig.getOtherShoppingListItems()
        } else {
            shoppingListsWithConfig.getAllShoppingListItems()
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
}

data class CopyProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val location: ShoppingLocation = ShoppingLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false
)