package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListNameState {

    private var editShoppingListName by mutableStateOf(EditShoppingListName())

    var screenData by mutableStateOf(EditShoppingListNameScreenData())
        private set

    fun populate(editShoppingListName: EditShoppingListName) {
        this.editShoppingListName = editShoppingListName

        val headerText: UiText = if (editShoppingListName.hasName()) {
            UiText.FromResources(R.string.editShoppingListName_header_editShoppingListName)
        } else {
            UiText.FromResources(R.string.editShoppingListName_header_addShoppingListName)
        }

        screenData = EditShoppingListNameScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = editShoppingListName.getFieldName().toTextFieldValue(),
            fontSize = editShoppingListName.getFontSize()
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(nameValue = nameValue)
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        screenData = screenData.copy(screenState = ScreenState.Saving)
        val shoppingList = editShoppingListName.createShoppingList(
            name = screenData.nameValue.text
        ).getOrNull()

        return if (shoppingList == null) {
            Result.failure(Exception())
        } else {
            Result.success(shoppingList)
        }
    }
}

data class EditShoppingListNameScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)