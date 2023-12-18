package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper

class TrashState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var shoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var selectedUids: List<String>? by mutableStateOf(null)
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

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var oldFontSize: FontSize by mutableStateOf(FontSize.DefaultValue)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        shoppingLists = UiShoppingListsMapper.toSortedShoppingListItems(shoppingListsWithConfig)
        selectedUids = null
        displayProducts = userPreferences.displayShoppingsProducts
        displayCompleted = userPreferences.displayCompleted
        coloredCheckbox = userPreferences.coloredCheckbox
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        smartphoneScreen = shoppingListsWithConfig.getDeviceConfig().getDeviceSize().isSmartphoneScreen()
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        oldFontSize = userPreferences.fontSize
        waiting = false
    }

    fun onAllShoppingListsSelected(selected: Boolean) {
        selectedUids = if (selected) shoppingListsWithConfig.getShoppingUids() else null
    }

    fun onShoppingListSelected(selected: Boolean, uid: String) {
        val uids = (selectedUids?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(uid) else remove(uid)
        }
        selectedUids = if (uids.isEmpty()) null else uids
    }

    fun onWaiting() {
        waiting = true
    }
}