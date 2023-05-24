package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.ui.utils.isNotEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class EditShoppingListTotalState {

    private var editShoppingListTotal by mutableStateOf(EditShoppingListTotal())

    var screenData by mutableStateOf(EditShoppingListTotalScreenData())
        private set

    fun populate(editShoppingListTotal: EditShoppingListTotal) {
        this.editShoppingListTotal = editShoppingListTotal

        val headerText: UiText = if (editShoppingListTotal.totalFormatted()) {
            UiText.FromResources(R.string.editShoppingListTotal_header_editShoppingListTotal)
        } else {
            UiText.FromResources(R.string.editShoppingListTotal_header_addShoppingListTotal)
        }

        val totalText: String = if (editShoppingListTotal.total().isEmpty()) {
            ""
        } else {
            editShoppingListTotal.total().valueToString()
        }

        screenData = EditShoppingListTotalScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            totalValue = TextFieldValue(
                text = totalText,
                selection = TextRange(totalText.length),
                composition = TextRange(totalText.length)
            ),
            fontSize = editShoppingListTotal.preferences.fontSize
        )
    }

    fun changeTotalValue(totalValue: TextFieldValue) {
        screenData = screenData.copy(totalValue = totalValue)
    }

    fun getShoppingListResult(): Result<ShoppingList> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        val total = Money(
            value = screenData.totalValue.toFloatOrZero(),
            currency = editShoppingListTotal.preferences.currency
        )

        val success = (editShoppingListTotal.shoppingList ?: ShoppingList()).copy(
            total = total,
            totalFormatted = screenData.totalValue.isNotEmpty(),
            lastModified = System.currentTimeMillis()
        )

        return Result.success(success)
    }
}

data class EditShoppingListTotalScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val totalValue: TextFieldValue = TextFieldValue(),
    val fontSize: FontSize = FontSize.MEDIUM
)