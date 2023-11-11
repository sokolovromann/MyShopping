package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.utils.getAllShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getSelectedTotal
import ru.sokolovromann.myshopping.ui.utils.getTotalText

class ArchiveState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var screenData by mutableStateOf(ArchiveScreenData())
        private set

    fun showLoading() {
        screenData = ArchiveScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        val totalText: UiText = if (userPreferences.displayMoney) {
            shoppingListsWithConfig.getTotalText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (userPreferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = ArchiveScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            displayTotal = userPreferences.displayTotal,
            fontSize = userPreferences.fontSize
        )
    }

    fun showShoppingLists(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        val totalText: UiText = if (userPreferences.displayMoney) {
            shoppingListsWithConfig.getTotalText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (userPreferences.shoppingsMultiColumns) {
            UiText.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
        } else {
            UiText.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
        }

        screenData = ArchiveScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingListsWithConfig.getAllShoppingListItems(),
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            totalText = totalText,
            multiColumns = userPreferences.shoppingsMultiColumns,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            displayTotal = userPreferences.displayTotal,
            fontSize = userPreferences.fontSize,
            showHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
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
            shoppingLists = shoppingListsWithConfig.getAllShoppingListItems(DisplayCompleted.LAST),
            showHiddenShoppingLists = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectShoppingList(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (shoppingListsWithConfig.getUserPreferences().displayMoney) {
            shoppingListsWithConfig.getSelectedTotal(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllShoppingLists() {
        val uids = shoppingListsWithConfig.getShoppingUids()

        val totalText = if (shoppingListsWithConfig.getUserPreferences().displayMoney) {
            shoppingListsWithConfig.getSelectedTotal(uids)
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

        val totalText = if (shoppingListsWithConfig.getUserPreferences().displayMoney) {
            if (checkedUids == null) {
                shoppingListsWithConfig.getTotalText()
            } else {
                shoppingListsWithConfig.getSelectedTotal(checkedUids)
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
        val totalText = if (shoppingListsWithConfig.getUserPreferences().displayMoney) {
            shoppingListsWithConfig.getTotalText()
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