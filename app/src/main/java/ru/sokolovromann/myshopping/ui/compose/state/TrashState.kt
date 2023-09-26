package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems

class TrashState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(TrashScreenData())
        private set

    fun showLoading() {
        screenData = TrashScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists

        screenData = TrashScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            showBottomBar = false,
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            displayTotal = shoppingLists.getDisplayTotal(),
            fontSize = shoppingLists.getFontSize()
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists

        val shoppingListItems = when (shoppingLists.getDisplayCompleted()) {
            DisplayCompleted.HIDE -> shoppingLists.getAllShoppingListItems()
            else -> shoppingLists.getAllShoppingListItems()
        }

        screenData = TrashScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingListItems,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            showBottomBar = shoppingLists.displayMoney(),
            multiColumns = shoppingLists.isMultiColumns(),
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            displayTotal = shoppingLists.getDisplayTotal(),
            fontSize = shoppingLists.getFontSize()
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
        val uids = shoppingLists.getUids()
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