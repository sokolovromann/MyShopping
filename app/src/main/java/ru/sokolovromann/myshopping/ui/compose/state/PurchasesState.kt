package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class PurchasesState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

    var screenData by mutableStateOf(PurchasesScreenData())
        private set

    fun showLoading() {
        screenData = PurchasesScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences) {
        shoppingLists = ShoppingLists(preferences = preferences)

        screenData = PurchasesScreenData(
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

        screenData = PurchasesScreenData(
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

    fun showPurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showPurchasesMenu = false
        )
    }

    fun showSelectingMenu() {
        screenData = screenData.copy(
            showSelectingMenu = true,
            showPurchasesMenu = false
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

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }
        screenData = screenData.copy(selectedUids = uids)
    }

    fun selectAllShoppingLists() {
        val uids = shoppingLists.shoppingLists.map { it.uid }
        screenData = screenData.copy(
            selectedUids = uids,
            showSelectingMenu = false
        )
    }

    fun selectCompletedShoppingLists() {
        val uids = shoppingLists.shoppingLists
            .filter { it.completed }
            .map { it.uid }
        screenData = screenData.copy(
            selectedUids = uids,
            showSelectingMenu = false
        )
    }

    fun selectActiveShoppingLists() {
        val uids = shoppingLists.shoppingLists
            .filter { !it.completed }
            .map { it.uid }
        screenData = screenData.copy(
            selectedUids = uids,
            showSelectingMenu = false
        )
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

    fun hidePurchasesMenu() {
        screenData = screenData.copy(showPurchasesMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideSelectingMenu() {
        screenData = screenData.copy(showSelectingMenu = false)
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

    fun getShoppingListResult(): Result<ShoppingList> {
        val position = shoppingLists.shoppingListsLastPosition?.plus(1) ?: 0
        val success = ShoppingList(position = position)
        return Result.success(success)
    }

    fun getShoppingListsUpResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        val formatShoppingList = shoppingLists.formatShoppingLists()
        return if (formatShoppingList.size < 2) {
            Result.failure(Exception())
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in formatShoppingList.indices) {
                val shoppingList = formatShoppingList[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = formatShoppingList[currentIndex].copy(
                position = formatShoppingList[previousIndex].position,
                lastModified = lastModified
            )
            val previousShoppingList = formatShoppingList[previousIndex].copy(
                position = formatShoppingList[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, previousShoppingList)
            Result.success(success)
        }
    }

    fun getShoppingListsDownResult(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        val formatShoppingList = shoppingLists.formatShoppingLists()
        return if (formatShoppingList.size < 2) {
            Result.failure(Exception())
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in formatShoppingList.indices) {
                val shoppingList = formatShoppingList[index]

                currentIndex = index
                if (index < formatShoppingList.lastIndex) {
                    nextIndex = index + 1
                }

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = formatShoppingList[currentIndex].copy(
                position = formatShoppingList[nextIndex].position,
                lastModified = lastModified
            )
            val nextShoppingList = formatShoppingList[nextIndex].copy(
                position = formatShoppingList[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, nextShoppingList)
            Result.success(success)
        }
    }
}

data class PurchasesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showPurchasesMenu: Boolean = false,
    val showSort: Boolean = false,
    val showSelectingMenu: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showHiddenShoppingLists: Boolean = false,
    val selectedUids: List<String>? = null
)