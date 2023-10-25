package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.*
import java.util.*

class ProductsState {

    private var products by mutableStateOf(Products())

    private var savedSelectedUid by mutableStateOf("")

    var screenData by mutableStateOf(ProductsScreenData())
        private set

    fun showLoading() {
        screenData = ProductsScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(products: Products) {
        this.products = products

        val totalText = if (products.displayMoney()) {
            products.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (products.isMultiColumns()) {
            UiText.FromResources(R.string.products_action_disableProductsMultiColumns)
        } else {
            UiText.FromResources(R.string.products_action_enableProductsMultiColumns)
        }

        val shoppingListName: UiText = if (products.getDisplayName().isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromString(products.getDisplayName())
        }
        val location = products.getShoppingLocation()
        screenData = ProductsScreenData(
            screenState = ScreenState.Nothing,
            shoppingListName = shoppingListName,
            shoppingListLocation = location.toShoppingListLocation(),
            shoppingListCompleted = products.isCompleted(),
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            reminderText = toReminderText(products.getCalendarReminder()),
            smartphoneScreen = products.isSmartphoneScreen(),
            coloredCheckbox = products.isColoredCheckbox(),
            displayCompleted = products.getDisplayCompleted(),
            sort = products.getSort(),
            automaticSorting = products.isAutomaticSorting(),
            displayTotal = products.getDisplayTotal(),
            totalFormatted = products.isTotalFormatted(),
            fontSize = products.getFontSize(),
            displayMoney = products.displayMoney(),
            completedWithCheckbox = products.isCompletedWithCheckbox()
        )
    }

    fun showProducts(products: Products) {
        this.products = products
        val totalText = if (products.displayMoney()) {
            products.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (products.isMultiColumns()) {
            UiText.FromResources(R.string.products_action_disableProductsMultiColumns)
        } else {
            UiText.FromResources(R.string.products_action_enableProductsMultiColumns)
        }

        val shoppingListName: UiText = if (products.getDisplayName().isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromString(products.getDisplayName())
        }

        val location = products.getShoppingLocation()

        val selectedUids = if (savedSelectedUid.isEmpty()) {
            null
        } else {
            listOf(savedSelectedUid)
        }

        screenData = ProductsScreenData(
            screenState = ScreenState.Showing,
            shoppingListName = shoppingListName,
            shoppingListLocation = location.toShoppingListLocation(),
            shoppingListCompleted = products.isCompleted(),
            pinnedProducts = products.getActivePinnedProductItems(),
            otherProducts = products.getOtherProductItems(),
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            reminderText = toReminderText(products.getCalendarReminder()),
            multiColumns = products.isMultiColumns(),
            multiColumnsText = multiColumnsText,
            smartphoneScreen = products.isSmartphoneScreen(),
            coloredCheckbox = products.isColoredCheckbox(),
            displayCompleted = products.getDisplayCompleted(),
            sort = products.getSort(),
            automaticSorting = products.isAutomaticSorting(),
            displayTotal = products.getDisplayTotal(),
            totalFormatted = products.isTotalFormatted(),
            showHiddenProducts = products.isDisplayHiddenProducts(),
            fontSize = products.getFontSize(),
            displayMoney = products.displayMoney(),
            completedWithCheckbox = products.isCompletedWithCheckbox(),
            selectedUids = selectedUids
        )
    }

    fun showProductsMenu() {
        screenData = screenData.copy(showProductsMenu = true)
    }

    fun showSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showProductsMenu = false
        )
    }

    fun showShoppingMenu() {
        screenData = screenData.copy(
            showShoppingMenu = true,
            showProductsMenu = false
        )
    }

    fun displayHiddenProducts() {
        screenData = screenData.copy(
            otherProducts = products.getOtherProductItems(),
            showHiddenProducts = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectProduct(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (products.displayMoney()) {
            products.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllProducts() {
        val uids = products.getProductUids()

        val totalText = if (products.displayMoney()) {
            products.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids,
            showSelectedMenu = false
        )
    }

    fun unselectProduct(uid: String) {
        savedSelectedUid = ""

        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { remove(uid) }
        val checkedUids = if (uids.isEmpty()) null else uids

        val totalText = if (products.displayMoney()) {
            if (checkedUids == null) {
                products.calculateTotalToText()
            } else {
                products.calculateTotalToText(checkedUids)
            }
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = checkedUids
        )
    }

    fun unselectAllProducts() {
        val totalText = if (products.displayMoney()) {
            products.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        savedSelectedUid = ""
        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = null
        )
    }

    fun hideProductsMenu() {
        screenData = screenData.copy(showProductsMenu = false)
    }

    fun hideSelectedMenu() {
        screenData = screenData.copy(showSelectedMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideShoppingMenu() {
        screenData = screenData.copy(showShoppingMenu = false)
    }

    fun hideDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun getShareProductsResult(): Result<String> {
        return products.getShareText()
    }

    fun getProductsUpResult(uid: String): Result<Pair<Product, Product>> {
        return products.moveProductUp(uid).onSuccess {
            savedSelectedUid = uid
        }
    }

    fun getProductsDownResult(uid: String): Result<Pair<Product, Product>> {
        return products.moveProductDown(uid).onSuccess {
            savedSelectedUid = uid
        }
    }

    fun sortProductsResult(sortBy: SortBy): Result<List<Product>> {
        val sort = screenData.sort.copy(sortBy = sortBy)
        return products.sortProducts(sort)
    }

    fun reverseSortProductsResult(): Result<List<Product>> {
        return products.reverseSortProducts()
    }

    fun getShoppingListUid(): String {
        return products.getShoppingListUid()
    }

    fun isEditProductAfterCompleted(): Boolean {
        return products.editProductAfterCompleted()
    }

    fun getCopyShoppingListResult(): Result<ShoppingList> {
        return products.copyShoppingList()
    }

    private fun toReminderText(reminder: Calendar?): UiText {
        return reminder?.getDisplayDateAndTime() ?: UiText.Nothing
    }

    private fun toProductNotFoundText(location: ShoppingLocation): UiText = when(location) {
        ShoppingLocation.PURCHASES -> UiText.FromResources(R.string.products_text_purchasesProductsNotFound)

        ShoppingLocation.ARCHIVE -> UiText.FromResources(R.string.products_text_archiveProductsNotFound)

        ShoppingLocation.TRASH -> UiText.FromResources(R.string.products_text_trashProductsNotFound)
    }
}

data class ProductsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingListName: UiText = UiText.Nothing,
    val shoppingListLocation: ShoppingListLocation? = null,
    val shoppingListCompleted: Boolean = false,
    val pinnedProducts: List<ProductItem> = listOf(),
    val otherProducts: List<ProductItem> = listOf(),
    val productsNotFoundText: UiText = UiText.Nothing,
    val showProductsMenu: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val reminderText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val multiColumnsText: UiText = UiText.Nothing,
    val smartphoneScreen: Boolean = true,
    val coloredCheckbox: Boolean = false,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val showSort: Boolean = false,
    val sort: Sort = Sort(),
    val automaticSorting: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val totalFormatted: Boolean = false,
    val showHiddenProducts: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val displayMoney: Boolean = true,
    val completedWithCheckbox: Boolean = false,
    val selectedUids: List<String>? = null,
    val showSelectedMenu: Boolean = false,
    val showShoppingMenu: Boolean = false
) {

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
}