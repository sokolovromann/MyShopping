package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListNameState {

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var screenData by mutableStateOf(EditShoppingListNameScreenData())
        private set

    fun populate(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig

        val shoppingName = shoppingListWithConfig.getShopping().name
        val headerText: UiText = if (shoppingName.isNotEmpty()) {
            UiText.FromResources(R.string.editShoppingListName_header_editShoppingListName)
        } else {
            UiText.FromResources(R.string.editShoppingListName_header_addShoppingListName)
        }

        screenData = EditShoppingListNameScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = shoppingName.toTextFieldValue(),
            fontSize = shoppingListWithConfig.getUserPreferences().fontSize
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(nameValue = nameValue)
    }

    fun getShoppingUid(): String {
        return shoppingListWithConfig.getShopping().uid
    }
}

data class EditShoppingListNameScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)