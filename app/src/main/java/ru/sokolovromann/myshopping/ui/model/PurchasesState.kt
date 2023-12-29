package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class PurchasesState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    private var savedSelectedUid: String by mutableStateOf("")

    var pinnedShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var otherShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var displayHiddenShoppingLists: Boolean by mutableStateOf(false)
        private set

    var selectedUids: List<String>? by mutableStateOf(null)
        private set

    var displayProducts: DisplayProducts by mutableStateOf(DisplayProducts.DefaultValue)
        private set

    var displayCompleted: DisplayCompleted by mutableStateOf(DisplayCompleted.DefaultValue)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var totalValue: SelectedValue<DisplayTotal>? by mutableStateOf(SelectedValue(DisplayTotal.DefaultValue))
        private set

    var expandedDisplayTotal: Boolean by mutableStateOf(false)
        private set

    var multiColumnsValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var smartphoneScreen: Boolean by mutableStateOf(false)
        private set

    var expandedPurchasesMenu: Boolean by mutableStateOf(false)
        private set

    var expandedItemMoreMenu: Boolean by mutableStateOf(false)
        private set

    var expandedSort: Boolean by mutableStateOf(false)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(shoppingListsWithConfig)
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(shoppingListsWithConfig)
        displayHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        selectedUids = if (savedSelectedUid.isEmpty()) null else listOf(savedSelectedUid)
        displayProducts = userPreferences.displayShoppingsProducts
        displayCompleted = userPreferences.displayCompleted
        coloredCheckbox = userPreferences.coloredCheckbox
        totalValue = toTotalSelectedValue(shoppingListsWithConfig.getTotal())
        expandedDisplayTotal = false
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen()
        expandedPurchasesMenu = false
        expandedItemMoreMenu = false
        expandedSort = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        waiting = false
    }

    fun onSelectDisplayTotal(expanded: Boolean) {
        expandedDisplayTotal = expanded
    }

    fun onSelectSort(expanded: Boolean) {
        expandedSort = expanded
        expandedPurchasesMenu = false
    }

    fun onShowPurchasesMenu(expanded: Boolean) {
        expandedPurchasesMenu = expanded
        expandedSort = false
    }

    fun onShowItemMoreMenu(expanded: Boolean) {
        expandedItemMoreMenu = expanded
        expandedPurchasesMenu = false
        expandedSort = false
    }

    fun onAllShoppingListsSelected(selected: Boolean) {
        if (!selected) {
            savedSelectedUid = ""
        }
        expandedItemMoreMenu = false

        val uids = if (selected) shoppingListsWithConfig.getShoppingUids() else null
        selectedUids = uids

        val total = if (uids == null) {
            shoppingListsWithConfig.getTotal()
        } else {
            shoppingListsWithConfig.calculateTotalByUids(uids)
        }
        totalValue = if (uids == null) {
            toTotalSelectedValue(total)
        } else {
            totalValue?.copy(
                text = UiString.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.getDisplayValue())
            )
        }
    }

    fun onShoppingListSelected(selected: Boolean, uid: String) {
        savedSelectedUid = if (selected) uid else ""

        val uids = (selectedUids?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(uid) else remove(uid)
        }
        val checkedUids = if (uids.isEmpty()) null else uids
        selectedUids = checkedUids

        val total = if (checkedUids == null) {
            shoppingListsWithConfig.getTotal()
        } else {
            shoppingListsWithConfig.calculateTotalByUids(checkedUids)
        }
        totalValue = if (checkedUids == null) {
            toTotalSelectedValue(total)
        } else {
            totalValue?.copy(
                text = UiString.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.getDisplayValue())
            )
        }
    }

    fun onShowHiddenShoppingLists(display: Boolean) {
        val displayCompleted = if (display) DisplayCompleted.LAST else DisplayCompleted.HIDE
        otherShoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            displayCompleted = displayCompleted
        )
        displayHiddenShoppingLists = display
    }

    fun onWaiting() {
        waiting = true
    }

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

    fun isNotFound(): Boolean {
        return pinnedShoppingLists.isEmpty() && otherShoppingLists.isEmpty()
    }

    fun expandedItemFavoriteMenu(uid: String): Boolean {
        return selectedUids?.count() == 1 && selectedUids?.contains(uid) == true
    }

    private fun toTotalSelectedValue(total: Money): SelectedValue<DisplayTotal>? {
        return UiShoppingListsMapper.toTotalValue(
            total = total,
            totalFormatted = false,
            userPreferences = shoppingListsWithConfig.getUserPreferences()
        )
    }
}