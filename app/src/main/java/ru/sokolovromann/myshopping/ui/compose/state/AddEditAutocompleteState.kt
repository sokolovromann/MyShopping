package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class AddEditAutocompleteState {

    private var addEditAutocomplete by mutableStateOf(AddEditAutocomplete())

    var screenData by mutableStateOf(AddEditAutocompleteScreenData())
        private set

    fun populate(addEditAutocomplete: AddEditAutocomplete) {
        this.addEditAutocomplete = addEditAutocomplete

        val headerText: UiText = if (addEditAutocomplete.isNewAutocomplete()) {
            UiText.FromResources(R.string.addEditAutocomplete_header_addAutocomplete)
        } else {
            UiText.FromResources(R.string.addEditAutocomplete_header_editAutocomplete)
        }

        screenData = AddEditAutocompleteScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = addEditAutocomplete.getFieldName().toTextFieldValue(),
            showNameError = false,
            fontSize = addEditAutocomplete.getFontSize()
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        val result = addEditAutocomplete.createAutocomplete(screenData.nameValue.text).getOrNull()
        return if (result == null) {
            screenData = screenData.copy(showNameError = true)
            Result.failure(Exception())
        } else {
            Result.success(result)
        }
    }
}

data class AddEditAutocompleteScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val showNameError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)