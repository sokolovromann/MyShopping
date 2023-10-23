package ru.sokolovromann.myshopping.ui.compose.state

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.utils.getActivePinnedShoppingListItems
import ru.sokolovromann.myshopping.ui.utils.getOtherShoppingListItems

class ProductsWidgetConfigState {

    private var shoppingLists by mutableStateOf(ShoppingLists())

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

    fun showNotFound(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        nightTheme = shoppingLists.isNightTheme()
        loading = false

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            fontSize = shoppingLists.getFontSize()
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists

        loading = false
        nightTheme = shoppingLists.isNightTheme()

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = shoppingLists.getActivePinnedShoppingListItems(),
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(),
            displayProducts = shoppingLists.getDisplayProducts(),
            displayCompleted = shoppingLists.getDisplayCompleted(),
            coloredCheckbox = shoppingLists.isColoredCheckbox(),
            multiColumns = shoppingLists.isMultiColumns(),
            smartphoneScreen = shoppingLists.isSmartphoneScreen(),
            fontSize = shoppingLists.getFontSize()
        )
    }

    fun getShoppingListResult(uid: String): Result<ShoppingList> {
        return shoppingLists.getShoppingList(uid)
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