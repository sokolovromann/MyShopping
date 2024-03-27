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
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListNameState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var header: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var nameValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val name = shoppingListWithConfig.getShopping().name
        header = if (name.isEmpty()) {
            UiString.FromResources(R.string.editShoppingListName_header_addShoppingListName)
        } else {
            UiString.FromResources(R.string.editShoppingListName_header_editShoppingListName)
        }
        nameValue = name.toTextFieldValue()
        waiting = false
        fontSize = UiAppConfigMapper.toUiFontSize(shoppingListWithConfig.getUserPreferences().fontSize)
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