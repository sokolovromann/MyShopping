package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class ArchiveState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(ArchiveScreenData())
        private set

    fun showLoading() {
        screenData = ArchiveScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences) {
        shoppingLists = ShoppingLists(preferences = preferences)
        screenData = ArchiveScreenData(
            screenState = ScreenState.Nothing,
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.preferences

        val totalText: UiText = if (preferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val showHiddenShoppingLists = preferences.displayCompletedPurchases == DisplayCompleted.HIDE
                && shoppingLists.hasHiddenShoppingLists()

        screenData = ArchiveScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            totalText = totalText,
            multiColumns = preferences.shoppingsMultiColumns,
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            fontSize = preferences.fontSize,
            showHiddenShoppingLists = showHiddenShoppingLists
        )
    }

    fun showShoppingListMenu(uid: String) {
        screenData = screenData.copy(shoppingListMenuUid = uid)
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

    fun showToPurchases() {
        screenData = screenData.copy(
            showToPurchases = true,
            showArchiveMenu = false
        )
    }

    fun showToTrash() {
        screenData = screenData.copy(
            showToTrash = true,
            showArchiveMenu = false
        )
    }

    fun displayHiddenShoppingLists() {
        screenData = screenData.copy(
            shoppingLists = shoppingLists.getShoppingListItems(DisplayCompleted.LAST),
            showHiddenShoppingLists = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun hideShoppingListMenu() {
        screenData = screenData.copy(shoppingListMenuUid = null)
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

    fun hideToPurchases() {
        screenData = screenData.copy(showToPurchases = false)
    }

    fun hideToTrash() {
        screenData = screenData.copy(showToTrash = false)
    }

    fun sortShoppingListsResult(sortBy: SortBy): Result<List<ShoppingList>> {
        val sortShoppingLists = shoppingLists.shoppingLists.sortShoppingLists(sort = Sort(sortBy))
        return if (sortShoppingLists.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = sortShoppingLists.mapIndexed { index, shoppingList ->
                shoppingList.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun getShoppingListsResult(): Result<List<ShoppingList>> {
        val resultShoppingLists = shoppingLists.shoppingLists
        return if (resultShoppingLists.isEmpty()) {
            Result.failure(Exception())
        } else {
            Result.success(resultShoppingLists)
        }
    }
}

data class ArchiveScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val shoppingListMenuUid: String? = null,
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showArchiveMenu: Boolean = false,
    val showSort: Boolean = false,
    val showToPurchases: Boolean = false,
    val showToTrash: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false
)