package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList

class EditShoppingListNameState {

    private var editShoppingListName by mutableStateOf(EditShoppingListName())

    var screenData by mutableStateOf(EditShoppingListNameScreenData())
        private set

    fun populate(editShoppingListName: EditShoppingListName) {
        this.editShoppingListName = editShoppingListName

        val headerText: UiText = if (editShoppingListName.shoppingList == null) {
            UiText.FromResources(R.string.editShoppingListName_header_addShoppingListName)
        } else {
            UiText.FromResources(R.string.editShoppingListName_header_editShoppingListName)
        }
        val name = editShoppingListName.name()

        screenData = EditShoppingListNameScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            fontSize = editShoppingListName.preferences.fontSize
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(nameValue = nameValue)
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        screenData = screenData.copy(screenState = ScreenState.Saving)
        val success = (editShoppingListName.shoppingList ?: ShoppingList()).copy(
            name = screenData.nameValue.text.trim(),
            lastModified = System.currentTimeMillis()
        )

        return Result.success(success)
    }
}

data class EditShoppingListNameScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)