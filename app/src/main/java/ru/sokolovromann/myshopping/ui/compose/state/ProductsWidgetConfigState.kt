package ru.sokolovromann.myshopping.ui.compose.state

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.AppPreferences
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.data.repository.model.FontSize
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

    fun showNotFound(preferences: AppPreferences) {
        shoppingLists = ShoppingLists(preferences = preferences)
        nightTheme = preferences.nightTheme
        loading = false

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Nothing,
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
            smartphoneScreen = preferences.smartphoneScreen,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists) {
        this.shoppingLists = shoppingLists
        val preferences = shoppingLists.preferences

        loading = false
        nightTheme = preferences.nightTheme

        screenData = ProductsWidgetConfigScreenData(
            screenState = ScreenState.Showing,
            pinnedShoppingLists = shoppingLists.getActivePinnedShoppingListItems(),
            otherShoppingLists = shoppingLists.getOtherShoppingListItems(),
            displayProducts = preferences.displayShoppingsProducts,
            highlightCheckbox = preferences.highlightCheckbox,
            multiColumns = preferences.shoppingsMultiColumns,
            smartphoneScreen = preferences.smartphoneScreen,
            fontSize = preferences.fontSize
        )
    }

    fun getShoppingListResult(uid: String): Result<ShoppingList> {
        val shoppingList = shoppingLists.shoppingLists.find { it.uid == uid }
        return if (shoppingList == null) {
            Result.failure(Exception())
        } else {
            Result.success(shoppingList)
        }
    }
}

data class ProductsWidgetConfigScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val pinnedShoppingLists: List<ShoppingListItem> = listOf(),
    val otherShoppingLists: List<ShoppingListItem> = listOf(),
    val displayProducts: DisplayProducts = DisplayProducts.DefaultValue,
    val highlightCheckbox: Boolean = false,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val fontSize: FontSize = FontSize.MEDIUM
)