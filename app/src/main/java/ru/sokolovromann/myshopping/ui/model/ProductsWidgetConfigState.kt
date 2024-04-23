package ru.sokolovromann.myshopping.ui.model

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper
import ru.sokolovromann.myshopping.ui.theme.FontSizeOffset

class ProductsWidgetConfigState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var widgetId: Int by mutableStateOf(AppWidgetManager.INVALID_APPWIDGET_ID)
        private set

    var pinnedShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var otherShoppingLists: List<ShoppingListItem> by  mutableStateOf(listOf())
        private set

    var nightTheme: NightTheme by mutableStateOf(NightTheme.DefaultValue)
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

    var fontSizeOffset: FontSizeOffset by mutableStateOf(FontSizeOffset())
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(false)
        private set

    fun populate(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.getUserPreferences()
        pinnedShoppingLists = UiShoppingListsMapper.toPinnedSortedShoppingListItems(shoppingListsWithConfig)
        otherShoppingLists = UiShoppingListsMapper.toOtherSortedShoppingListItems(shoppingListsWithConfig)
        nightTheme = userPreferences.nightTheme
        displayProducts = userPreferences.displayShoppingsProducts
        displayCompleted = userPreferences.appDisplayCompleted
        strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts
        coloredCheckbox = userPreferences.coloredCheckbox
        multiColumnsValue = UiShoppingListsMapper.toMultiColumnsValue(userPreferences.shoppingsMultiColumns)
        deviceSize = shoppingListsWithConfig.getDeviceConfig().getDeviceSize()
        fontSizeOffset = UiAppConfigMapper.toFontSizeOffset(userPreferences.appFontSize)
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.appFontSize)
        waiting = false
    }

    fun saveWidgetId(widgetId: Int) {
        this.widgetId = widgetId
        waiting = true
    }

    fun isNotFound(): Boolean {
        return shoppingListsWithConfig.isEmpty()
    }
}