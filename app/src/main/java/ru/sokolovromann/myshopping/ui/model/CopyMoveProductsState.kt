package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class CopyMoveProductsState {

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

    var strikethroughCompletedProducts: Boolean by mutableStateOf(false)
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var multiColumnsValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var locationValue: SelectedValue<ShoppingLocation> by mutableStateOf(SelectedValue(
        ShoppingLocation.DefaultValue))
        private set

    var expandedLocation: Boolean by mutableStateOf(false)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig, location: ShoppingLocation) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            location = location
        )
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            location = location
        )
        displayHiddenShoppingLists = shoppingListsWithConfig.hasHiddenShoppingLists()
        displayProducts = userPreferences.displayShoppingsProducts
        displayCompleted = userPreferences.appDisplayCompleted
        strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts
        coloredCheckbox = userPreferences.coloredCheckbox
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        deviceSize = shoppingListsWithConfig.getDeviceConfig().getDeviceSize()
        locationValue = UiShoppingListsMapper.toLocationValue(location)
        expandedLocation = false
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.appFontSize)
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
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            location = locationValue.selected,
            displayCompleted = displayCompleted
        )
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(
            shoppingListsWithConfig = shoppingListsWithConfig,
            location = locationValue.selected,
            displayCompleted = displayCompleted
        )
        displayHiddenShoppingLists = display
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return shoppingListsWithConfig.isEmpty()
    }
}