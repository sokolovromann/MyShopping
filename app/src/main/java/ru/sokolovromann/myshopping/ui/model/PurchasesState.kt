package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class PurchasesState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    private var savedSelectedUid: String by mutableStateOf("")

    var pinnedShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var otherShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var notFoundText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var displayHiddenShoppingLists: Boolean by mutableStateOf(false)
        private set

    var selectedUids: List<String>? by mutableStateOf(null)
        private set

    var displayProducts: DisplayProducts by mutableStateOf(DisplayProducts.DefaultValue)
        private set

    var expandedDisplayProducts: Boolean by mutableStateOf(false)
        private set

    var displayCompleted: DisplayCompleted by mutableStateOf(DisplayCompleted.DefaultValue)
        private set

    var strikethroughCompletedProducts: Boolean by mutableStateOf(false)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var totalValue: SelectedValue<DisplayTotal>? by mutableStateOf(SelectedValue(DisplayTotal.DefaultValue))
        private set

    var expandedDisplayTotal: Boolean by mutableStateOf(false)
        private set

    var expandedViewMenu: Boolean by mutableStateOf(false)
        private set

    var multiColumnsValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var expandedPurchasesMenu: Boolean by mutableStateOf(false)
        private set

    var expandedItemMoreMenu: Boolean by mutableStateOf(false)
        private set

    var sortValue: SelectedValue<Sort> by mutableStateOf(SelectedValue(Sort()))
        private set

    var sortFormatted: Boolean by mutableStateOf(false)
        private set

    var expandedSort: Boolean by mutableStateOf(false)
        private set

    var searchValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var displaySearch: Boolean by mutableStateOf(false)
        private set

    var expandedMarkAsMenu: Boolean by mutableStateOf(false)
        private set

    var afterAddShopping: AfterAddShopping by mutableStateOf(AfterAddShopping.DefaultValue)
        private set

    var swipeShoppingLeft: SwipeShopping by mutableStateOf(SwipeShopping.DefaultValue)
        private set

    var swipeShoppingRight: SwipeShopping by mutableStateOf(SwipeShopping.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(shoppingListsWithConfig)
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(shoppingListsWithConfig)
        notFoundText = toNotFoundText()
        displayHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        selectedUids = if (savedSelectedUid.isEmpty()) null else listOf(savedSelectedUid)
        displayProducts = userPreferences.displayShoppingsProducts
        expandedDisplayProducts = false
        displayCompleted = userPreferences.appDisplayCompleted
        strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts
        coloredCheckbox = userPreferences.coloredCheckbox
        totalValue = toTotalSelectedValue(shoppingListsWithConfig.getTotal())
        expandedDisplayTotal = false
        expandedViewMenu = false
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        deviceSize = shoppingListsWithConfig.getDeviceConfig().getDeviceSize()
        expandedPurchasesMenu = false
        expandedItemMoreMenu = false
        sortValue = toSortValue(userPreferences.shoppingsSort)
        sortFormatted = userPreferences.shoppingsSortFormatted
        expandedMarkAsMenu = false
        afterAddShopping = userPreferences.afterAddShopping
        swipeShoppingLeft = userPreferences.swipeShoppingLeft
        swipeShoppingRight = userPreferences.swipeShoppingRight
        waiting = false
    }

    fun onSelectDisplayProducts(expanded: Boolean) {
        expandedDisplayProducts = expanded
        expandedPurchasesMenu = false
    }

    fun onSelectDisplayTotal(expanded: Boolean) {
        expandedDisplayTotal = expanded
    }

    fun onSelectView(expanded: Boolean) {
        expandedViewMenu = expanded
        expandedPurchasesMenu = false
    }

    fun onSelectSort(expanded: Boolean) {
        expandedSort = expanded
        expandedPurchasesMenu = false
    }

    fun onSearch() {
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(
            search = searchValue.text,
            shoppingListsWithConfig = shoppingListsWithConfig
        )
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(
            search = searchValue.text,
            shoppingListsWithConfig = shoppingListsWithConfig
        )
        notFoundText = toNotFoundText()
    }

    fun onSearchValueChanged(value: TextFieldValue) {
        searchValue = value
    }

    fun onShowSearch(display: Boolean) {
        if (!display) {
            pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(shoppingListsWithConfig)
            otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(shoppingListsWithConfig)
            notFoundText = toNotFoundText()
            searchValue = TextFieldValue()
        }

        displaySearch = display
        expandedPurchasesMenu = false
        expandedItemMoreMenu = false
        expandedSort = false
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
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            search = null,
            displayEmptyShoppings = display
        )
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            search = null,
            displayCompleted = displayCompleted,
            displayEmptyShoppings = display
        )
        displayHiddenShoppingLists = !display
    }

    fun onSelectMarkAsMenu(expanded: Boolean) {
        expandedMarkAsMenu = expanded
        expandedItemMoreMenu = false
        expandedPurchasesMenu = false
        expandedSort = false
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
        return if (displaySearch) {
            pinnedShoppingLists.isEmpty() && otherShoppingLists.isEmpty()
        } else {
            shoppingListsWithConfig.isEmpty()
        }
    }

    fun expandedItemFavoriteMenu(uid: String): Boolean {
        return selectedUids?.count() == 1 && selectedUids?.contains(uid) == true
    }

    fun isShoppingListCompleted(uid: String): Boolean? {
        return shoppingListsWithConfig.getSortedShoppingLists().find { it.shopping.uid == uid }?.isCompleted()
    }

    fun getAfterShoppingCompleted(): AfterShoppingCompleted {
        return shoppingListsWithConfig.getUserPreferences().afterShoppingCompleted
    }

    private fun toNotFoundText(): UiString {
        return if (displaySearch) {
            UiString.FromResources(R.string.shoppingLists_text_searchNotFound)
        } else {
            UiString.FromResources(R.string.shoppingLists_text_purchasesShoppingListsNotFound)
        }
    }

    private fun toTotalSelectedValue(total: Money): SelectedValue<DisplayTotal>? {
        return UiShoppingListsMapper.toTotalValue(
            total = total,
            totalFormatted = false,
            userPreferences = shoppingListsWithConfig.getUserPreferences()
        )
    }

    private fun toSortValue(sort: Sort): SelectedValue<Sort> {
        return SelectedValue(
            selected = sort,
            text = when (sort.sortBy) {
                SortBy.POSITION -> UiString.FromString("")
                SortBy.CREATED -> UiString.FromResources(R.string.shoppingLists_action_sortByCreated)
                SortBy.LAST_MODIFIED -> UiString.FromResources(R.string.shoppingLists_action_sortByLastModified)
                SortBy.NAME -> UiString.FromResources(R.string.shoppingLists_action_sortByName)
                SortBy.TOTAL -> UiString.FromResources(R.string.shoppingLists_action_sortByTotal)
            }
        )
    }
}