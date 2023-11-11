package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListTotalState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var screenData by mutableStateOf(EditShoppingListTotalScreenData())
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        val headerText: UiText = if (shopping.totalFormatted) {
            UiText.FromResources(R.string.editShoppingListTotal_header_editShoppingListTotal)
        } else {
            UiText.FromResources(R.string.editShoppingListTotal_header_addShoppingListTotal)
        }

        val totalValue = if (shopping.totalFormatted) {
            shopping.total.toTextFieldValue()
        } else {
            "".toTextFieldValue()
        }
        screenData = EditShoppingListTotalScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            totalValue = totalValue,
            fontSize = shoppingListWithConfig.getUserPreferences().fontSize
        )
    }

    fun changeTotalValue(totalValue: TextFieldValue) {
        screenData = screenData.copy(totalValue = totalValue)
    }

    fun getShoppingUid(): String {
        return shoppingListWithConfig.getShopping().uid
    }

    fun getTotal(): Money {
        return shoppingListWithConfig.getShopping().total.copy(
            value = screenData.totalValue.toFloatOrZero()
        )
    }
}

data class EditShoppingListTotalScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val totalValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)