package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
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

    fun showNotFound(appConfig: AppConfig) {
        shoppingLists = ShoppingLists(appConfig = appConfig)
        val preferences = shoppingLists.appConfig.userPreferences

        val totalText: UiText = if (preferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (preferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = ArchiveScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompleted,
            coloredCheckbox = preferences.coloredCheckbox,
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.appConfig.userPreferences

        val totalText: UiText = if (preferences.displayMoney) {
            shoppingLists.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (preferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        val showHiddenShoppingLists = preferences.displayCompleted == DisplayCompleted.HIDE
                && shoppingLists.hasHiddenShoppingLists()

        screenData = ArchiveScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getAllShoppingListItems(splitByPinned = false),
            displayProducts = preferences.displayShoppingsProducts,
            displayCompleted = preferences.displayCompleted,
            coloredCheckbox = preferences.coloredCheckbox,
            totalText = totalText,
            multiColumns = preferences.shoppingsMultiColumns,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingLists.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize,
            showHiddenShoppingLists = showHiddenShoppingLists
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
            shoppingLists = shoppingLists.getAllShoppingListItems(false, DisplayCompleted.LAST),
            showHiddenShoppingLists = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
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
        val uids = shoppingLists.shoppingLists.map { it.uid }

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
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

        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
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
        val totalText = if (shoppingLists.appConfig.userPreferences.displayMoney) {
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

    fun reverseSortShoppingListsResult(): Result<List<ShoppingList>> {
        val sortShoppingLists = shoppingLists.getAllShoppingLists(false).reversed()
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