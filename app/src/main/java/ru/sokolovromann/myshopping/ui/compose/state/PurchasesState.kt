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
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

class PurchasesState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    private var savedSelectedUid by mutableStateOf("")

    var screenData by mutableStateOf(PurchasesScreenData())
        private set

    fun showLoading() {
        screenData = PurchasesScreenData(screenState = ScreenState.Loading)
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

        screenData = PurchasesScreenData(
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

        val selectedUids = if (savedSelectedUid.isEmpty()) {
            null
        } else {
            listOf(savedSelectedUid)
        }

        screenData = PurchasesScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = shoppingLists.getActivePinnedShoppingListItems(),
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(),
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            totalText = totalText,
            multiColumns = shoppingLists.isMultiColumns(),
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            displayTotal = shoppingLists.getDisplayTotal(),
            fontSize = shoppingLists.getFontSize(),
            showHiddenShoppingLists = shoppingLists.displayHiddenShoppingLists(),
            selectedUids = selectedUids
        )
    }

    fun showPurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showPurchasesMenu = false
        )
    }

    fun showSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = true)
    }

    fun displayHiddenShoppingLists() {
        screenData = screenData.copy(
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(),
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
            selectedUids = uids,
            showSelectedMenu = false
        )
    }

    fun unselectShoppingList(uid: String) {
        savedSelectedUid = ""

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

        savedSelectedUid = ""
        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = null
        )
    }

    fun hideDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun hidePurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = false)
    }

    fun sortShoppingListsResult(sortBy: SortBy): Result<List<ShoppingList>> {
        val sort = Sort(sortBy)
        return shoppingLists.sortShoppingLists(sort)
    }

    fun reverseSortShoppingListsResult(): Result<List<ShoppingList>> {
        return shoppingLists.reverseSortShoppingLists()
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        return shoppingLists.createShoppingList()
    }

    fun getCopyShoppingListsResult(): Result<List<ShoppingList>> {
        val uids = screenData.selectedUids
        return shoppingLists.copyShoppingLists(uids)
    }

    fun getShoppingListsUpResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        return shoppingLists.moveShoppingListUp(uid).onSuccess {
            savedSelectedUid = uid
        }
    }

    fun getShoppingListsDownResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        return shoppingLists.moveShoppingListDown(uid).onSuccess {
            savedSelectedUid = uid
        }
    }
}

data class PurchasesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val multiColumnsText: UiText = UiText.Nothing,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showPurchasesMenu: Boolean = false,
    val showSort: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false,
    val selectedUids: List<String>? = null,
    val showSelectedMenu: Boolean = false
) {

    fun isOnlyPinned(): Boolean {
        var notPinned = false
        selectedUids?.forEach { uid ->
            if (otherShoppingLists.find { it.uid == uid } != null) {
                notPinned = true
                return@forEach
            }
        }
        return pinnedShoppingLists.isNotEmpty() && !notPinned
    }
}