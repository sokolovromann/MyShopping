package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingPeriod
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class ArchiveState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var shoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
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

    var expandedArchiveMenu: Boolean by mutableStateOf(false)
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

    var archivePeriod: SelectedValue<ShoppingPeriod> by mutableStateOf(SelectedValue(ShoppingPeriod.DefaultValue))
        private set

    var expandedArchivePeriod: Boolean by mutableStateOf(false)
        private set

    var swipeShoppingLeft: SwipeShopping by mutableStateOf(SwipeShopping.DefaultValue)
        private set

    var swipeShoppingRight: SwipeShopping by mutableStateOf(SwipeShopping.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig, period: ShoppingPeriod) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        shoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(shoppingListsWithConfig)
        notFoundText = toNotFoundText()
        displayHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        selectedUids = null
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
        expandedArchiveMenu = false
        sortValue = toSortValue(userPreferences.shoppingsSort)
        sortFormatted = userPreferences.shoppingsSortFormatted
        archivePeriod = toArchivePeriodValue(period)
        expandedArchivePeriod = false
        swipeShoppingLeft = userPreferences.swipeShoppingLeft
        swipeShoppingRight = userPreferences.swipeShoppingRight
        waiting = false
    }

    fun onSelectDisplayProducts(expanded: Boolean) {
        expandedDisplayProducts = expanded
        expandedArchiveMenu = false
    }

    fun onSelectView(expanded: Boolean) {
        expandedViewMenu = expanded
        expandedArchiveMenu = false
    }

    fun onSelectDisplayTotal(expanded: Boolean) {
        expandedDisplayTotal = expanded
    }

    fun onSelectSort(expanded: Boolean) {
        expandedSort = expanded
        expandedArchiveMenu = false
    }

    fun onSearch() {
        shoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(
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
            shoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(shoppingListsWithConfig)
            notFoundText = toNotFoundText()
            searchValue = TextFieldValue()
        }

        displaySearch = display
        expandedArchiveMenu = false
        expandedSort = false
    }

    fun onShowArchiveMenu(expanded: Boolean) {
        expandedArchiveMenu = expanded
        expandedSort = false
    }

    fun onAllShoppingListsSelected(selected: Boolean) {
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
        shoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            displayCompleted = displayCompleted,
            displayEmptyShoppings = display
        )
        displayHiddenShoppingLists = !display
    }

    fun onSelectArchivePeriod(expanded: Boolean) {
        expandedArchivePeriod = expanded
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return if (displaySearch) {
            shoppingLists.isEmpty()
        } else {
            shoppingListsWithConfig.isEmpty()
        }
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
            UiString.FromResources(R.string.shoppingLists_text_archiveShoppingListsNotFound)
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

    private fun toArchivePeriodValue(period: ShoppingPeriod): SelectedValue<ShoppingPeriod> {
        return SelectedValue(
            selected = period,
            text = when (period) {
                ShoppingPeriod.ONE_MONTH -> UiString.FromResources(R.string.shoppingLists_action_selectOneMonthPeriod)
                ShoppingPeriod.THREE_MONTHS -> UiString.FromResources(R.string.shoppingLists_action_selectThreeMonthsPeriod)
                ShoppingPeriod.SIX_MONTHS -> UiString.FromResources(R.string.shoppingLists_action_selectSixMonthsPeriod)
                ShoppingPeriod.ONE_YEAR -> UiString.FromResources(R.string.shoppingLists_action_selectOneYearPeriod)
                ShoppingPeriod.ALL_TIME -> UiString.FromResources(R.string.shoppingLists_action_selectAllTimePeriod)
            }
        )
    }
}