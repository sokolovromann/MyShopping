package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class EditShoppingListTotalState {

    private var editShoppingListTotal by mutableStateOf(EditShoppingListTotal())

    var screenData by mutableStateOf(EditShoppingListTotalScreenData())
        private set

    fun populate(editShoppingListTotal: EditShoppingListTotal) {
        this.editShoppingListTotal = editShoppingListTotal

        val headerText: UiText = if (editShoppingListTotal.isTotalFormatted()) {
            UiText.FromResources(R.string.editShoppingListTotal_header_editShoppingListTotal)
        } else {
            UiText.FromResources(R.string.editShoppingListTotal_header_addShoppingListTotal)
        }

        screenData = EditShoppingListTotalScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            totalValue = editShoppingListTotal.getFieldTotal().toTextFieldValue(),
            fontSize = editShoppingListTotal.getFontSize()
        )
    }

    fun changeTotalValue(totalValue: TextFieldValue) {
        screenData = screenData.copy(totalValue = totalValue)
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        screenData = screenData.copy(screenState = ScreenState.Saving)
        val shoppingList = editShoppingListTotal.createShoppingList(
            total = screenData.totalValue.toFloatOrZero()
        ).getOrNull()

        return if (shoppingList == null) {
            Result.failure(Exception())
        } else {
            Result.success(shoppingList)
        }
    }
}

data class EditShoppingListTotalScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val totalValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)