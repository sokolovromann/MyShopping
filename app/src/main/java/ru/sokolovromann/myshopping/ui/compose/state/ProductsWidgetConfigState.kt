package ru.sokolovromann.myshopping.ui.compose.state

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

class ProductsWidgetConfigState {

    private var shoppingListsWithConfig by mutableStateOf(ShoppingListsWithConfig())

    var screenData by mutableStateOf(ProductsWidgetConfigScreenData())
        private set

    var widgetId by mutableStateOf(AppWidgetManager.INVALID_APPWIDGET_ID)

    var loading by mutableStateOf(false)
        private set

    var nightTheme by mutableStateOf(false)
        private set

    fun onCreate(widgetId: Int) {
        this.widgetId = widgetId
        loading = true

        screenData = ProductsWidgetConfigScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.appConfig.userPreferences
        nightTheme = userPreferences.nightTheme
        loading = false

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            smartphoneScreen = shoppingListsWithConfig.appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen(),
            fontSize = userPreferences.fontSize
        )
    }

    fun showShoppingLists(shoppingListsWithConfig: ShoppingListsWithConfig) {
        this.shoppingListsWithConfig = shoppingListsWithConfig

        val userPreferences = shoppingListsWithConfig.appConfig.userPreferences
        nightTheme = userPreferences.nightTheme
        loading = false

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = shoppingListsWithConfig.getActivePinnedShoppingListItems(),
            otherShoppingLists = shoppingListsWithConfig.getOtherShoppingListItems(),
            displayProducts = userPreferences.displayShoppingsProducts,
            displayCompleted = userPreferences.displayCompleted,
            coloredCheckbox = userPreferences.coloredCheckbox,
            multiColumns = userPreferences.shoppingsMultiColumns,
            smartphoneScreen = shoppingListsWithConfig.appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen(),
            fontSize = userPreferences.fontSize
        )
    }
}

data class ProductsWidgetConfigScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    val coloredCheckbox: Boolean = false,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val fontSize: FontSize = FontSize.MEDIUM
)