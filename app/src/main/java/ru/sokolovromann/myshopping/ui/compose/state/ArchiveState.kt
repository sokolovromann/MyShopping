package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class ArchiveState {

    var screenData by mutableStateOf(ArchiveScreenData())
        private set

    fun showLoading() {
        screenData = ArchiveScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: ShoppingListPreferences) {
        screenData = ArchiveScreenData(
            screenState = ScreenState.Nothing,
            showBottomBar = false,
            sort = preferences.sort,
            displayTotal = preferences.displayTotal,
            displayCompleted = preferences.displayCompleted,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        val preferences = shoppingLists.preferences

        screenData = ArchiveScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            totalText = shoppingLists.calculateTotalToText(),
            showBottomBar = preferences.displayMoney,
            multiColumns = preferences.multiColumns,
            sort = preferences.sort,
            displayTotal = preferences.displayTotal,
            displayCompleted = preferences.displayCompleted,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingListMenu(uid: String) {
        screenData = screenData.copy(shoppingListMenuUid = uid)
    }

    fun showSort() {
        screenData = screenData.copy(showSort = true)
    }

    fun showDisplayTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun showDisplayCompleted() {
        screenData = screenData.copy(showDisplayCompleted = true)
    }

    fun hideShoppingListMenu() {
        screenData = screenData.copy(shoppingListMenuUid = null)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideDisplayTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun hideDisplayCompleted() {
        screenData = screenData.copy(showDisplayCompleted = false)
    }
}

data class ArchiveScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val shoppingListMenuUid: String? = null,
    val totalText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val sort: Sort = Sort(),
    val showSort: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val showDisplayCompleted: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val showBottomBar: Boolean = true
)