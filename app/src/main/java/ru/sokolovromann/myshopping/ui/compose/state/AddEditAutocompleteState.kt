package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.utils.isEmpty

class AddEditAutocompleteState {

    private var autocomplete by mutableStateOf(Autocomplete())

    var screenData by mutableStateOf(AddEditAutocompleteScreenData())
        private set

    fun populate(addEditAutocomplete: AddEditAutocomplete) {
        autocomplete = addEditAutocomplete.autocomplete ?: Autocomplete()

        val headerText: UiText = if (addEditAutocomplete.autocomplete == null) {
            UiText.FromResources(R.string.addEditAutocomplete_header_addAutocomplete)
        } else {
            UiText.FromResources(R.string.addEditAutocomplete_header_editAutocomplete)
        }
        val name = autocomplete.name

        screenData = AddEditAutocompleteScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            showNameError = false,
            fontSize = addEditAutocomplete.appConfig.userPreferences.fontSize
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        return if (screenData.nameValue.isEmpty()) {
            screenData = screenData.copy(showNameError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)
            val success = autocomplete.copy(
                name = screenData.nameValue.text.trim(),
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
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