package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper
import ru.sokolovromann.myshopping.ui.utils.toUiString
import ru.sokolovromann.myshopping.widget.WidgetFontSizeOffset

class ProductsWidgetState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var pinnedProducts: List<ProductWidgetItem> by mutableStateOf(listOf())
        private set

    var otherProducts: List<ProductWidgetItem> by mutableStateOf(listOf())
        private set

    var nameText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var totalText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var coloredCheckbox: Boolean by mutableStateOf(false)
        private set

    var completedWithCheckbox: Boolean by mutableStateOf(false)
        private set

    var completed: Boolean by mutableStateOf(false)
        private set

    var displayCompleted: DisplayCompleted by mutableStateOf(DisplayCompleted.DefaultValue)
        private set

    var strikethroughCompletedProducts: Boolean by mutableStateOf(false)
        private set

    var displayMoney: Boolean by mutableStateOf(false)
        private set

    var nightTheme: NightTheme by mutableStateOf(NightTheme.DefaultValue)
        private set

    var fontSizeOffset: WidgetFontSizeOffset by mutableStateOf(WidgetFontSizeOffset())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        pinnedProducts = UiShoppingListsMapper.toPinnedSortedProductWidgetItems(shoppingListWithConfig)
        otherProducts = UiShoppingListsMapper.toOtherSortedProductWidgetItems(
            shoppingListWithConfig,
            userPreferences.widgetDisplayCompleted
        )
        nameText = shopping.name.toUiString()
        totalText = shopping.total.getDisplayValue().toUiString()
        coloredCheckbox = userPreferences.coloredCheckbox
        completedWithCheckbox = userPreferences.completedWithCheckbox
        completed = shoppingListWithConfig.isCompleted()
        displayCompleted = userPreferences.widgetDisplayCompleted
        strikethroughCompletedProducts = userPreferences.strikethroughCompletedProducts
        displayMoney = userPreferences.displayMoney
        nightTheme = userPreferences.nightTheme
        fontSizeOffset = UiAppConfigMapper.toWidgetFontSizeOffset(userPreferences.widgetFontSize)
        waiting = false
    }

    fun isNotFound(): Boolean {
        return shoppingListWithConfig.isProductsEmpty()
    }
}