package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class AddEditAutocompleteState {

    private var autocompleteWithConfig by mutableStateOf(AutocompleteWithConfig())

    var screenData by mutableStateOf(AddEditAutocompleteScreenData())
        private set

    fun populate(autocompleteWithConfig: AutocompleteWithConfig) {
        this.autocompleteWithConfig = autocompleteWithConfig

        val headerText: UiText = if (autocompleteWithConfig.isEmpty()) {
            UiText.FromResources(R.string.addEditAutocomplete_header_addAutocomplete)
        } else {
            UiText.FromResources(R.string.addEditAutocomplete_header_editAutocomplete)
        }

        screenData = AddEditAutocompleteScreenData(
            screenState = ScreenState.Showing,
            headerText = headerText,
            nameValue = autocompleteWithConfig.autocomplete.name.toTextFieldValue(),
            showNameError = false,
            fontSize = autocompleteWithConfig.appConfig.userPreferences.fontSize
        )
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun showNameError() {
        screenData = screenData.copy(showNameError = true)
    }

    fun getCurrentAutocomplete(): Autocomplete {
        return autocompleteWithConfig.autocomplete.copy(
            name = screenData.nameValue.text,
            lastModified = DateTime.getCurrentDateTime()
        )
    }
}

data class AddEditAutocompleteScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val headerText: UiText = UiText.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val showNameError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)