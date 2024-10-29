package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
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

    var budgetText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var shoppingListPinnedValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
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

    var strikethroughCompletedProducts: Boolean by mutableStateOf(false)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var completedWithCheckbox: Boolean by mutableStateOf(false)
        private set

    var totalValue: SelectedValue<DisplayTotal>? by mutableStateOf(SelectedValue(DisplayTotal.DefaultValue))
        private set

    var displayLongTotal: Boolean by mutableStateOf(false)
        private set

    var expandedDisplayTotal: Boolean by mutableStateOf(false)
        private set

    var totalFormatted: Boolean by mutableStateOf(false)
        private set

    var displayMoney: Boolean by mutableStateOf(false)
        private set

    var expandedViewMenu: Boolean by mutableStateOf(false)
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

    var searchValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var displaySearch: Boolean by mutableStateOf(false)
        private set

    var expandedMarkAsMenu: Boolean by mutableStateOf(false)
        private set

    var expandedShareProducts: Boolean by mutableStateOf(false)
        private set

    var displayListOfAutocompletes: Boolean by mutableStateOf(false)
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
        budgetText = toShoppingBudget(shopping.budget, shopping.budgetProducts)
        shoppingListPinnedValue = UiShoppingListsMapper.toShoppingListPinned(shopping.pinned)
        locationValue = UiShoppingListsMapper.toLocationValue(shopping.location)
        completed = shoppingListWithConfig.isCompleted()
        displayHiddenProducts = shoppingListWithConfig.hasHiddenProducts()
        selectedUids = if (savedSelectedUid.isEmpty()) null else listOf(savedSelectedUid)
        displayCompleted = userPreferences.appDisplayCompleted
        strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts
        coloredCheckbox = userPreferences.coloredCheckbox
        completedWithCheckbox = userPreferences.completedWithCheckbox
        totalValue = selectedUids?.let { toTotalSelectedValueByProductUids(it) }
            ?: toTotalSelectedValue(shoppingListWithConfig.getShopping().total)
        displayLongTotal = userPreferences.displayLongTotal
        expandedDisplayTotal = false
        totalFormatted = shopping.totalFormatted
        displayMoney = userPreferences.displayMoney
        expandedViewMenu = false
        multiColumnsValue = toMultiColumnsValue(userPreferences.productsMultiColumns)
        deviceSize = shoppingListWithConfig.getDeviceConfig().getDeviceSize()
        expandedProductsMenu = false
        expandedItemMoreMenu = false
        expandedShoppingMenu = false
        sortValue = toSortValue(shopping.sort)
        sortFormatted = shopping.sortFormatted
        expandedMarkAsMenu = false
        expandedShareProducts = false
        displayListOfAutocompletes = userPreferences.displayListOfAutocompletes
        waiting = false
    }

    fun onSelectDisplayTotal(expanded: Boolean) {
        expandedDisplayTotal = expanded
    }

    fun onSelectView(expanded: Boolean) {
        expandedViewMenu = expanded
        expandedProductsMenu = false
        expandedShoppingMenu = false
    }

    fun onSelectSort(expanded: Boolean) {
        expandedSort = expanded
        expandedProductsMenu = false
        expandedShoppingMenu = false
    }

    fun onSearch() {
        pinnedProducts = UiShoppingListsMapper.toPinnedSortedProductItems(
            search = searchValue.text,
            shoppingListWithConfig = shoppingListWithConfig
        )
        otherProducts = UiShoppingListsMapper.toOtherSortedProductItems(
            search = searchValue.text,
            shoppingListWithConfig = shoppingListWithConfig
        )
        notFoundText = toNotFoundText(shoppingListWithConfig.getShopping().location)
    }

    fun onSearchValueChanged(value: TextFieldValue) {
        searchValue = value
    }

    fun onShowSearch(display: Boolean) {
        if (!display) {
            pinnedProducts = UiShoppingListsMapper.toPinnedSortedProductItems(shoppingListWithConfig)
            otherProducts = UiShoppingListsMapper.toOtherSortedProductItems(shoppingListWithConfig)
            notFoundText = toNotFoundText(shoppingListWithConfig.getShopping().location)
            searchValue = TextFieldValue()
        }

        displaySearch = display
        expandedProductsMenu = false
        expandedShoppingMenu = false
        expandedSort = false
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

        totalValue = if (uids == null) {
            toTotalSelectedValue(shoppingListWithConfig.getShopping().total)
        } else {
            toTotalSelectedValueByProductUids(uids)
        }
    }

    fun onProductSelected(selected: Boolean, uid: String) {
        savedSelectedUid = if (selected) uid else ""

        val uids = (selectedUids?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(uid) else remove(uid)
        }
        val checkedUids = if (uids.isEmpty()) null else uids
        selectedUids = checkedUids

        totalValue = if (checkedUids == null) {
            toTotalSelectedValue(shoppingListWithConfig.getShopping().total)
        } else {
            toTotalSelectedValueByProductUids(uids)
        }
    }

    fun onShowHiddenProducts(display: Boolean) {
        val displayCompleted = if (display) DisplayCompleted.LAST else DisplayCompleted.HIDE
        otherProducts = UiShoppingListsMapper.toSortedProductItems(
            shoppingListWithConfig = shoppingListWithConfig,
            displayCompleted = displayCompleted
        )
        displayHiddenProducts = !display
    }

    fun onSelectMarkAsMenu(expanded: Boolean) {
        expandedMarkAsMenu = expanded
        expandedItemMoreMenu = false
        expandedProductsMenu = false
        expandedSort = false
    }

    fun onSelectShareProducts(expanded: Boolean) {
        expandedShareProducts = expanded
        expandedMarkAsMenu = false
        expandedItemMoreMenu = false
        expandedProductsMenu = false
        expandedSort = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getShareText(displayTotal: DisplayTotal): String {
        return UiShoppingListsMapper.toShoppingListString(shoppingListWithConfig, displayTotal)
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
        return if (displaySearch) {
            pinnedProducts.isEmpty() && otherProducts.isEmpty()
        } else {
            shoppingListWithConfig.isProductsEmpty()
        }
    }

    fun expandedItemFavoriteMenu(uid: String): Boolean {
        return selectedUids?.count() == 1 && selectedUids?.contains(uid) == true
    }

    fun isOverBudget(): Boolean {
        val budgetProducts = shoppingListWithConfig.getShopping().budgetProducts
        val budget = shoppingListWithConfig.getShopping().budget
        return if (budget.isEmpty()) {
            false
        } else {
            val total = shoppingListWithConfig.calculateTotalByDisplayTotal(budgetProducts)
            val totalValue = total.getFormattedValueWithoutSeparators().toFloat()
            totalValue > budget.getFormattedValueWithoutSeparators().toFloat() &&
                    shoppingListWithConfig.getUserPreferences().displayTotal == budgetProducts &&
                    !shoppingListWithConfig.getShopping().totalFormatted
        }
    }

    private fun toNotFoundText(location: ShoppingLocation): UiString {
        return if (displaySearch) {
            UiString.FromResources(R.string.products_text_searchNotFound)
        } else {
            when (location) {
                ShoppingLocation.PURCHASES -> UiString.FromResources(R.string.products_text_purchasesProductsNotFound)

                ShoppingLocation.ARCHIVE -> UiString.FromResources(R.string.products_text_archiveProductsNotFound)

                ShoppingLocation.TRASH -> UiString.FromResources(R.string.products_text_trashProductsNotFound)
            }
        }
    }

    private fun toTotalSelectedValue(total: Money): SelectedValue<DisplayTotal>? {
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        if (!userPreferences.displayMoney) {
            return null
        }

        val text: UiString = if (shoppingListWithConfig.getShopping().totalFormatted) {
            val totalWithoutDiscount = shoppingListWithConfig.getTotalWithoutDiscount()
            val discount = shoppingListWithConfig.getShopping().discount
            val builder = getLongTotalBuilder(totalWithoutDiscount, discount, total)
            UiString.FromResourcesWithArgs(R.string.products_text_totalFormatted, builder)

        } else {
            val id = when (userPreferences.displayTotal) {
                DisplayTotal.ALL -> R.string.products_text_allTotal
                DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
                DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
            }

            val cost = shoppingListWithConfig.calculateCostByDisplayTotal()
            val discounts = shoppingListWithConfig.calculateDiscountsByDisplayTotal()
            val taxRates = shoppingListWithConfig.calculateTaxRatesByDisplayTotal()
            val builder = getLongTotalBuilder(cost, discounts, taxRates, total)
            UiString.FromResourcesWithArgs(id, builder)
        }

        return SelectedValue(
            selected = userPreferences.displayTotal,
            text = text
        )
    }

    private fun toTotalSelectedValueByProductUids(uids: List<String>): SelectedValue<DisplayTotal>? {
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        if (!userPreferences.displayMoney) {
            return null
        }

        val cost = shoppingListWithConfig.calculateCostByProductUids(uids)
        val discounts = shoppingListWithConfig.calculateDiscountsByProductUids(uids)
        val taxRates = shoppingListWithConfig.calculateTaxRatesByProductUids(uids)
        val total = shoppingListWithConfig.calculateTotalByProductUids(uids)
        val builder = getLongTotalBuilder(cost, discounts, taxRates, total)

        return totalValue?.copy(
            text = UiString.FromResourcesWithArgs(R.string.products_text_selectedTotal, builder)
        )
    }

    private fun getLongTotalBuilder(
        totalWithoutDiscount: Money,
        discount: Money,
        total: Money
    ): StringBuilder {
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        val builder = StringBuilder()

        if (userPreferences.displayLongTotal && discount.isNotEmpty()) {
            builder.append(totalWithoutDiscount)
            builder.append(" - ")
            builder.append(discount)
            builder.append(" = ")
        }
        builder.append(total)

        return builder
    }

    private fun getLongTotalBuilder(
        cost: Money,
        discounts: Money,
        taxRates: Money,
        total: Money
    ): StringBuilder {
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        val builder = StringBuilder()
        if (userPreferences.displayLongTotal) {
            if (cost.isEmpty()) {
                builder.append(total)
            } else {
                if (discounts.isNotEmpty() || taxRates.isNotEmpty()) {
                    builder.append(cost)

                    if (discounts.isNotEmpty()) {
                        builder.append(" - ")
                        builder.append(discounts)
                    }

                    if (taxRates.isNotEmpty()) {
                        builder.append(" + ")
                        builder.append(taxRates)
                    }

                    builder.append(" = ")
                }

                builder.append(total)
            }
        } else {
            builder.append(total)
        }

        return builder
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

    private fun toShoppingBudget(budget: Money, budgetProducts: DisplayTotal): UiString {
        return if (budget.isEmpty() ||
            shoppingListWithConfig.getUserPreferences().displayTotal != budgetProducts ||
            shoppingListWithConfig.getShopping().totalFormatted
        ) {
            UiString.FromString("")
        } else {
            budget.getDisplayValue().toUiString()
        }
    }
}