package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class TrashState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(TrashScreenData())
        private set

    fun showLoading() {
        screenData = TrashScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences) {
        shoppingLists = ShoppingLists(preferences = preferences)
        screenData = TrashScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
            showBottomBar = false,
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.preferences

        val shoppingListItems = when (preferences.displayCompletedPurchases) {
            DisplayCompleted.FIRST, DisplayCompleted.LAST -> shoppingLists.getShoppingListItems()
            else -> shoppingLists.getShoppingListItems(DisplayCompleted.LAST)
        }

        screenData = TrashScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingListItems,
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
            showBottomBar = preferences.displayMoney,
            multiColumns = preferences.shoppingsMultiColumns,
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            fontSize = preferences.fontSize
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
        val uids = shoppingLists.shoppingLists.map { it.uid }
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
    val highlightCheckbox: Boolean = false,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showBottomBar: Boolean = true,
    val selectedUids: List<String>? = null
)