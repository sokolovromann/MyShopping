package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems

class TrashState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var screenData by mutableStateOf(TrashScreenData())
        private set

    fun showLoading() {
        screenData = TrashScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.appConfig.userPreferences
        screenData = TrashScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            showBottomBar = false,
            smartphoneScreen = shoppingListsWithConfig.appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen(),
            displayTotal = userPreferences.displayTotal,
            fontSize = userPreferences.fontSize
        )
    }

    fun showShoppingLists(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.appConfig.userPreferences
        screenData = TrashScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingListsWithConfig.getAllShoppingListItems(),
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            showBottomBar = userPreferences.displayMoney,
            multiColumns = userPreferences.shoppingsMultiColumns,
            smartphoneScreen = shoppingListsWithConfig.appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen(),
            displayTotal = userPreferences.displayTotal,
            fontSize = userPreferences.fontSize
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }
        screenData = screenData.copy(selectedUids = uids)
    }

    fun selectAllShoppingLists() {
        val uids = shoppingListsWithConfig.shoppingLists.map { it.shopping.uid }
        screenData = screenData.copy(selectedUids = uids)
    }

    fun unselectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { remove(uid) }
        val checkedUids = if (uids.isEmpty()) null else uids
        screenData = screenData.copy(selectedUids = checkedUids)
    }

    fun unselectAllShoppingLists() {
        screenData = screenData.copy(selectedUids = null)
    }

    fun hideDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }
}

data class TrashScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showBottomBar: Boolean = true,
    val selectedUids: List<String>? = null
)