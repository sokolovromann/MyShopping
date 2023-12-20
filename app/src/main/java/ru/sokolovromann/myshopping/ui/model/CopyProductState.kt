package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class CopyProductState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var savedProducts by mutableStateOf<List<Product>>(listOf())
        private set

    var pinnedShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var otherShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var displayHiddenShoppingLists: Boolean by mutableStateOf(false)
        private set

    var displayProducts: DisplayProducts by mutableStateOf(DisplayProducts.DefaultValue)
        private set

    var displayCompleted: DisplayCompleted by mutableStateOf(DisplayCompleted.DefaultValue)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var multiColumnsValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var smartphoneScreen: Boolean by mutableStateOf(false)
        private set

    var locationValue: SelectedValue<ShoppingLocation> by mutableStateOf(SelectedValue(ShoppingLocation.DefaultValue))
        private set

    var expandedLocation: Boolean by mutableStateOf(false)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var oldFontSize: FontSize by mutableStateOf(FontSize.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig, location: ShoppingLocation) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        pinnedShoppingLists = toPinnedShoppingListItems(location)
        otherShoppingLists = toOtherShoppingListItems(location)
        displayHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        displayProducts = userPreferences.displayShoppingsProducts
        displayCompleted = userPreferences.displayCompleted
        coloredCheckbox = userPreferences.coloredCheckbox
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen()
        locationValue = toLocationSelectedValue(location)
        expandedLocation = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        oldFontSize = userPreferences.fontSize
        waiting = false
    }

    fun saveProducts(products: List<Product>) {
        savedProducts = products
    }

    fun onSelectLocation(expanded: Boolean) {
        expandedLocation = expanded
    }

    fun onShowHiddenShoppingLists(display: Boolean) {
        val displayCompleted = if (display) DisplayCompleted.LAST else DisplayCompleted.HIDE
        pinnedShoppingLists = toPinnedShoppingListItems(locationValue.selected, displayCompleted)
        otherShoppingLists = toOtherShoppingListItems(locationValue.selected, displayCompleted)
        displayHiddenShoppingLists = display
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return pinnedShoppingLists.isEmpty() && otherShoppingLists.isEmpty()
    }

    private fun toPinnedShoppingListItems(
        location: ShoppingLocation,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            UiShoppingListsMapper.toPinnedSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        } else {
            listOf()
        }
    }

    private fun toOtherShoppingListItems(
        location: ShoppingLocation,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            UiShoppingListsMapper.toOtherSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        } else {
            UiShoppingListsMapper.toSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        }
    }

    private fun toLocationSelectedValue(location: ShoppingLocation): SelectedValue<ShoppingLocation> {
        return SelectedValue(
            selected = location,
            text = when (location) {
                ShoppingLocation.PURCHASES -> UiString.FromResources(R.string.shoppingLists_action_selectPurchasesLocation)
                ShoppingLocation.ARCHIVE -> UiString.FromResources(R.string.shoppingLists_action_selectArchiveLocation)
                ShoppingLocation.TRASH -> UiString.FromResources(R.string.shoppingLists_action_selectTrashLocation)
            }
        )
    }
}