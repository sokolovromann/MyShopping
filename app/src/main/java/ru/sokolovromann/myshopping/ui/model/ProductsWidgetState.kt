package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiShoppingListsMapper
import ru.sokolovromann.myshopping.ui.utils.toUiString

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

    var displayMoney: Boolean by mutableStateOf(false)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var forceLoad: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        pinnedProducts = UiShoppingListsMapper.toPinnedSortedProductWidgetItems(shoppingListWithConfig)
        otherProducts = UiShoppingListsMapper.toOtherSortedProductWidgetItems(shoppingListWithConfig)
        nameText = shopping.name.toUiString()
        totalText = shopping.total.getDisplayValue().toUiString()
        coloredCheckbox = userPreferences.coloredCheckbox
        completedWithCheckbox = userPreferences.completedWithCheckbox
        completed = shoppingListWithConfig.isCompleted()
        displayCompleted = userPreferences.displayCompleted
        displayMoney = userPreferences.displayMoney
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        waiting = false
        forceLoad = false
    }

    fun isNotFound(): Boolean {
        return shoppingListWithConfig.isProductsEmpty()
    }
}