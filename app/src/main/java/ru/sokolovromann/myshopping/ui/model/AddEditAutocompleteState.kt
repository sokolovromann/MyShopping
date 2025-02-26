package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.AutocompleteWithConfig
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class AddEditAutocompleteState {

    private var autocompleteWithConfig: AutocompleteWithConfig by mutableStateOf(AutocompleteWithConfig())

    var nameValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var nameError: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(autocompleteWithConfig: AutocompleteWithConfig) {
        this.autocompleteWithConfig = autocompleteWithConfig

        nameValue = autocompleteWithConfig.autocomplete.name.toTextFieldValue()
        nameError = false
        waiting = false
    }

    fun onNameValueChanged(value: TextFieldValue) {
        nameValue = value
        nameError = false
        waiting = false
    }

    fun onInvalidNameValue() {
        nameError = true
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun getAutocomplete(): Autocomplete {
        return autocompleteWithConfig.autocomplete.copy(
            name = nameValue.text,
            lastModified = DateTime.getCurrentDateTime()
        )
    }
}