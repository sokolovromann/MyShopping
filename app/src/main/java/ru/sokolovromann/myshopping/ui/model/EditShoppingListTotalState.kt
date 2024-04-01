package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListTotalState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var header: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var totalValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shopping = shoppingListWithConfig.getShopping()
        header = if (shopping.totalFormatted) {
            UiString.FromResources(R.string.editShoppingListTotal_header_editShoppingListTotal)
        } else {
            UiString.FromResources(R.string.editShoppingListTotal_header_addShoppingListTotal)
        }
        totalValue = if (shopping.totalFormatted) {
            shopping.total.toTextFieldValue()
        } else {
            "".toTextFieldValue()
        }
        waiting = false
        fontSize = UiAppConfigMapper.toUiFontSize(shoppingListWithConfig.getUserPreferences().appFontSize)
    }

    fun onTotalValueChanged(value: TextFieldValue) {
        totalValue = value
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentShopping(): Shopping {
        val total = shoppingListWithConfig.getShopping().total.copy(
            value = totalValue.toFloatOrZero()
        )
        return shoppingListWithConfig.getShopping().copy(
            total = total,
            totalFormatted = total.isNotEmpty(),
            lastModified = DateTime.getCurrentDateTime()
        )
    }
}