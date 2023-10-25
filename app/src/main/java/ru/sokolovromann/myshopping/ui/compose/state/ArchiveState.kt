package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems

class ArchiveState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(ArchiveScreenData())
        private set

    fun showLoading() {
        screenData = ArchiveScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists

        val totalText: UiText = if (shoppingLists.displayMoney()) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (shoppingLists.isMultiColumns()) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = ArchiveScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            displayTotal = shoppingLists.getDisplayTotal(),
            fontSize = shoppingLists.getFontSize()
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists

        val totalText: UiText = if (shoppingLists.displayMoney()) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (shoppingLists.isMultiColumns()) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = ArchiveScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getAllShoppingListItems(),
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            totalText = totalText,
            multiColumns = shoppingLists.isMultiColumns(),
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            displayTotal = shoppingLists.getDisplayTotal(),
            fontSize = shoppingLists.getFontSize(),
            showHiddenShoppingLists = shoppingLists.displayHiddenShoppingLists()
        )
    }

    fun showArchiveMenu() {
        screenData = screenData.copy(showArchiveMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showArchiveMenu = false
        )
    }

    fun displayHiddenShoppingLists() {
        screenData = screenData.copy(
            shoppingLists = shoppingLists.getAllShoppingListItems(),
            showHiddenShoppingLists = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (shoppingLists.displayMoney()) {
            shoppingLists.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllShoppingLists() {
        val uids = shoppingLists.getUids()

        val totalText = if (shoppingLists.displayMoney()) {
            shoppingLists.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun unselectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { remove(uid) }
        val checkedUids = if (uids.isEmpty()) null else uids

        val totalText = if (shoppingLists.displayMoney()) {
            if (checkedUids == null) {
                shoppingLists.calculateTotalToText()
            } else {
                shoppingLists.calculateTotalToText(checkedUids)
            }
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = checkedUids
        )
    }

    fun unselectAllShoppingLists() {
        val totalText = if (shoppingLists.displayMoney()) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = null
        )
    }

    fun hideDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun hideArchiveMenu() {
        screenData = screenData.copy(showArchiveMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun sortShoppingListsResult(sortBy: SortBy): Result<List<ShoppingList>> {
        val sort = Sort(sortBy)
        return shoppingLists.sortShoppingLists(sort)
    }

    fun reverseSortShoppingListsResult(): Result<List<ShoppingList>> {
        return shoppingLists.reverseSortShoppingLists()
    }
}

data class ArchiveScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val multiColumnsText: UiText = UiText.Nothing,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showArchiveMenu: Boolean = false,
    val showSort: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false,
    val selectedUids: List<String>? = null
)