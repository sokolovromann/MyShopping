package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListNameState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var nameValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var isFromPurchases: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig, isFromPurchases: Boolean?) {
        this.shoppingListWithConfig = shoppingListWithConfig

        nameValue = shoppingListWithConfig.getShopping().name.toTextFieldValue()
        this.isFromPurchases = isFromPurchases ?: false
        waiting = false
    }

    fun onNameValueChanged(value: TextFieldValue) {
        nameValue = value
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getCurrentShopping(): Shopping {
        return shoppingListWithConfig.getShopping().copy(
            name = nameValue.text,
            lastModified = DateTime.getCurrentDateTime()
        )
    }
}