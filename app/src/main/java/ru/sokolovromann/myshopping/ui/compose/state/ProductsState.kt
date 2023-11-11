package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.ui.utils.*
import java.util.*

class ProductsState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    private var savedSelectedUid by mutableStateOf("")

    var screenData by mutableStateOf(ProductsScreenData())
        private set

    fun showLoading() {
        screenData = ProductsScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig
        val shopping = shoppingListWithConfig.getShopping()
        val userPreference = shoppingListWithConfig.getUserPreferences()

        val totalText = if (userPreference.displayMoney) {
            shoppingListWithConfig.getTotal()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (userPreference.productsMultiColumns) {
            UiText.FromResources(R.string.products_action_disableProductsMultiColumns)
        } else {
            UiText.FromResources(R.string.products_action_enableProductsMultiColumns)
        }

        val location = shopping.location
        screenData = ProductsScreenData(
            screenState = ScreenState.Nothing,
            shoppingListName = shopping.name.toUiTextOrNothing(),
            shoppingLocation = location,
            shoppingListCompleted = shoppingListWithConfig.isCompleted(),
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            multiColumnsText = multiColumnsText,
            reminderText = toReminderText(shopping.reminder?.toCalendar()),
            smartphoneScreen = shoppingListWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            coloredCheckbox = userPreference.coloredCheckbox,
            displayCompleted = userPreference.displayCompleted,
            sort = shopping.sort,
            automaticSorting = shopping.sortFormatted,
            displayTotal = userPreference.displayTotal,
            totalFormatted = shopping.totalFormatted,
            fontSize = userPreference.fontSize,
            displayMoney = userPreference.displayMoney,
            completedWithCheckbox = userPreference.completedWithCheckbox
        )
    }

    fun showProducts(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig
        val shopping = shoppingListWithConfig.getShopping()
        val userPreference = shoppingListWithConfig.getUserPreferences()

        val totalText = if (userPreference.displayMoney) {
            shoppingListWithConfig.getTotal()
        } else {
            UiText.Nothing
        }

        val multiColumnsText: UiText = if (userPreference.productsMultiColumns) {
            UiText.FromResources(R.string.products_action_disableProductsMultiColumns)
        } else {
            UiText.FromResources(R.string.products_action_enableProductsMultiColumns)
        }

        val location = shopping.location

        val selectedUids = if (savedSelectedUid.isEmpty()) {
            null
        } else {
            listOf(savedSelectedUid)
        }

        screenData = ProductsScreenData(
            screenState = ScreenState.Showing,
            shoppingListName = shoppingListWithConfig.getShopping().name.toUiTextOrNothing(),
            shoppingLocation = location,
            shoppingListCompleted = shoppingListWithConfig.isCompleted(),
            pinnedProducts = shoppingListWithConfig.getActivePinnedProductItems(),
            otherProducts = shoppingListWithConfig.getOtherProductItems(),
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            reminderText = toReminderText(shopping.reminder?.toCalendar()),
            multiColumns = userPreference.productsMultiColumns,
            multiColumnsText = multiColumnsText,
            smartphoneScreen = shoppingListWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen(),
            coloredCheckbox = userPreference.coloredCheckbox,
            displayCompleted = userPreference.displayCompleted,
            sort = shopping.sort,
            automaticSorting = shopping.sortFormatted,
            displayTotal = userPreference.displayTotal,
            totalFormatted = shopping.totalFormatted,
            showHiddenProducts = shoppingListWithConfig.hasHiddenProducts(),
            fontSize = userPreference.fontSize,
            displayMoney = userPreference.displayMoney,
            completedWithCheckbox = userPreference.completedWithCheckbox,
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
            otherProducts = shoppingListWithConfig.getOtherProductItems(DisplayCompleted.LAST),
            showHiddenProducts = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectProduct(uid: String) {
        savedSelectedUid = uid

        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (shoppingListWithConfig.getUserPreferences().displayMoney) {
            shoppingListWithConfig.getSelectedTotal(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllProducts() {
        val uids = shoppingListWithConfig.getProductUids()

        val totalText = if (shoppingListWithConfig.getUserPreferences().displayMoney) {
            shoppingListWithConfig.getSelectedTotal(uids)
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

        val totalText = if (shoppingListWithConfig.getUserPreferences().displayMoney) {
            if (checkedUids == null) {
                shoppingListWithConfig.getTotal()
            } else {
                shoppingListWithConfig.getSelectedTotal(checkedUids)
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
        val totalText = if (shoppingListWithConfig.getUserPreferences().displayMoney) {
            shoppingListWithConfig.getTotal()
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
        return shoppingListWithConfig.getShareText()
    }

    fun getShoppingListUid(): String {
        return shoppingListWithConfig.getShopping().uid
    }

    fun isEditProductAfterCompleted(): Boolean {
        return shoppingListWithConfig.getUserPreferences().editProductAfterCompleted
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
    val shoppingLocation: ShoppingLocation? = null,
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