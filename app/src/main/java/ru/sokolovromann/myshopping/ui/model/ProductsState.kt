package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper
import ru.sokolovromann.myshopping.ui.utils.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.utils.toUiString

class ProductsState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    private var savedSelectedUid: String by mutableStateOf("")

    var pinnedProducts: List<ProductItem> by mutableStateOf(listOf())
        private set

    var otherProducts: List<ProductItem> by mutableStateOf(listOf())
        private set

    var notFoundText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var nameText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var reminderText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var locationValue: SelectedValue<ShoppingLocation> by mutableStateOf(SelectedValue(ShoppingLocation.DefaultValue))
        private set

    var completed: Boolean by mutableStateOf(false)
        private set

    var displayHiddenProducts: Boolean by mutableStateOf(false)
        private set

    var selectedUids: List<String>? by mutableStateOf(null)
        private set

    var displayCompleted: DisplayCompleted by mutableStateOf(DisplayCompleted.DefaultValue)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var completedWithCheckbox: Boolean by mutableStateOf(false)
        private set

    var totalValue: SelectedValue<DisplayTotal>? by mutableStateOf(SelectedValue(DisplayTotal.DefaultValue))
        private set

    var expandedDisplayTotal: Boolean by mutableStateOf(false)
        private set

    var totalFormatted: Boolean by mutableStateOf(false)
        private set

    var displayMoney: Boolean by mutableStateOf(false)
        private set

    var multiColumnsValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var expandedProductsMenu: Boolean by mutableStateOf(false)
        private set

    var expandedItemMoreMenu: Boolean by mutableStateOf(false)
        private set

    var expandedShoppingMenu: Boolean by mutableStateOf(false)
        private set

    var sortValue: SelectedValue<Sort> by mutableStateOf(SelectedValue(Sort()))
        private set

    var sortFormatted: Boolean by mutableStateOf(false)
        private set

    var expandedSort: Boolean by mutableStateOf(false)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        pinnedProducts = UiShoppingListsMapper.toPinnedSortedProductItems(shoppingListWithConfig)
        otherProducts = UiShoppingListsMapper.toOtherSortedProductItems(shoppingListWithConfig)
        notFoundText = toNotFoundText(shopping.location)
        nameText = shopping.name.toUiString()
        reminderText = shopping.reminder?.toCalendar()?.getDisplayDateAndTime() ?: UiString.FromString("")
        locationValue = UiShoppingListsMapper.toLocationValue(shopping.location)
        completed = shoppingListWithConfig.isCompleted()
        displayHiddenProducts = shoppingListWithConfig.hasHiddenProducts()
        selectedUids = if (savedSelectedUid.isEmpty()) null else listOf(savedSelectedUid)
        displayCompleted = userPreferences.displayCompleted
        coloredCheckbox = userPreferences.coloredCheckbox
        completedWithCheckbox = userPreferences.completedWithCheckbox
        totalValue = toTotalSelectedValue(shopping.total)
        expandedDisplayTotal = false
        totalFormatted = shopping.totalFormatted
        displayMoney = userPreferences.displayMoney
        multiColumnsValue = toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        deviceSize = shoppingListWithConfig.getDeviceConfig().getDeviceSize()
        expandedProductsMenu = false
        expandedItemMoreMenu = false
        expandedShoppingMenu = false
        sortValue = toSortValue(shopping.sort)
        sortFormatted = shopping.sortFormatted
        expandedSort = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        waiting = false
    }

    fun onSelectDisplayTotal(expanded: Boolean) {
        expandedDisplayTotal = expanded
    }

    fun onSelectSort(expanded: Boolean) {
        expandedSort = expanded
        expandedProductsMenu = false
        expandedShoppingMenu = false
    }

    fun onShowProductsMenu(expanded: Boolean) {
        expandedProductsMenu = expanded
        expandedShoppingMenu = false
        expandedSort = false
    }

    fun onShowItemMoreMenu(expanded: Boolean) {
        expandedItemMoreMenu = expanded
        expandedProductsMenu = false
        expandedShoppingMenu = false
        expandedSort = false
    }

    fun onShowShoppingMenu(expanded: Boolean) {
        expandedShoppingMenu = expanded
        expandedProductsMenu = false
        expandedSort = false
    }

    fun onAllProductsSelected(selected: Boolean) {
        if (!selected) {
            savedSelectedUid = ""
        }
        expandedItemMoreMenu = false

        val uids = if (selected) shoppingListWithConfig.getProductUids() else null
        selectedUids = uids

        val total = if (uids == null) {
            shoppingListWithConfig.getShopping().total
        } else {
            shoppingListWithConfig.calculateTotalByProductUids(uids)
        }
        totalValue = if (uids == null) {
            toTotalSelectedValue(total)
        } else {
            totalValue?.copy(
                text = UiString.FromResourcesWithArgs(R.string.products_text_selectedTotal, total.getDisplayValue())
            )
        }
    }

    fun onProductSelected(selected: Boolean, uid: String) {
        savedSelectedUid = if (selected) uid else ""

        val uids = (selectedUids?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(uid) else remove(uid)
        }
        val checkedUids = if (uids.isEmpty()) null else uids
        selectedUids = checkedUids

        val total = if (checkedUids == null) {
            shoppingListWithConfig.getShopping().total
        } else {
            shoppingListWithConfig.calculateTotalByProductUids(uids)
        }
        totalValue = if (checkedUids == null) {
            toTotalSelectedValue(total)
        } else {
            totalValue?.copy(
                text = UiString.FromResourcesWithArgs(R.string.products_text_selectedTotal, total.getDisplayValue())
            )
        }
    }

    fun onShowHiddenProducts(display: Boolean) {
        val displayCompleted = if (display) DisplayCompleted.LAST else DisplayCompleted.HIDE
        otherProducts = UiShoppingListsMapper.toSortedProductItems(
            shoppingListWithConfig = shoppingListWithConfig,
            displayCompleted = displayCompleted
        )
        displayHiddenProducts = display
    }

    fun onWaiting() {
        waiting = true
    }

    fun getShareText(): String {
        return UiShoppingListsMapper.toShoppingListString(shoppingListWithConfig)
    }

    fun isEditProductAfterCompleted(): Boolean {
        return shoppingListWithConfig.getUserPreferences().editProductAfterCompleted
    }

    fun isOnlyPinned(): Boolean {
        var notPinned = false
        selectedUids?.forEach { uid ->
            if (otherProducts.find { it.uid == uid } != null) {
                notPinned = true
                return@forEach
            }
        }
        return pinnedProducts.isNotEmpty() && !notPinned
    }

    fun isNotFound(): Boolean {
        return pinnedProducts.isEmpty() && otherProducts.isEmpty()
    }

    fun expandedItemFavoriteMenu(uid: String): Boolean {
        return selectedUids?.count() == 1 && selectedUids?.contains(uid) == true
    }

    private fun toNotFoundText(location: ShoppingLocation): UiString {
        return when (location) {
            ShoppingLocation.PURCHASES -> UiString.FromResources(R.string.products_text_purchasesProductsNotFound)

            ShoppingLocation.ARCHIVE -> UiString.FromResources(R.string.products_text_archiveProductsNotFound)

            ShoppingLocation.TRASH -> UiString.FromResources(R.string.products_text_trashProductsNotFound)
        }
    }

    private fun toTotalSelectedValue(total: Money): SelectedValue<DisplayTotal>? {
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        if (!userPreferences.displayMoney) {
            return null
        }

        val text: UiString = if (shoppingListWithConfig.getShopping().totalFormatted) {
            UiString.FromResourcesWithArgs(R.string.products_text_totalFormatted, total.getDisplayValue())
        } else {
            val id = when (userPreferences.displayTotal) {
                DisplayTotal.ALL -> R.string.products_text_allTotal
                DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
                DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
            }
            UiString.FromResourcesWithArgs(id, total.getDisplayValue())
        }

        return SelectedValue(
            selected = userPreferences.displayTotal,
            text = text
        )
    }

    private fun toMultiColumnsValue(multiColumns: Boolean): SelectedValue<Boolean> {
        return SelectedValue(
            selected = multiColumns,
            text = if (multiColumns) {
                UiString.FromResources(R.string.products_action_disableProductsMultiColumns)
            } else {
                UiString.FromResources(R.string.products_action_enableProductsMultiColumns)
            }
        )
    }

    private fun toSortValue(sort: Sort): SelectedValue<Sort> {
        return SelectedValue(
            selected = sort,
            text = when (sort.sortBy) {
                SortBy.POSITION -> UiString.FromString("")
                SortBy.CREATED -> UiString.FromResources(R.string.products_action_sortByCreated)
                SortBy.LAST_MODIFIED -> UiString.FromResources(R.string.products_action_sortByLastModified)
                SortBy.NAME -> UiString.FromResources(R.string.products_action_sortByName)
                SortBy.TOTAL -> UiString.FromResources(R.string.products_action_sortByTotal)
            }
        )
    }
}