package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.AddEditAutocompleteRepository
import ru.sokolovromann.myshopping.data.repository.model.AddEditAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.AutocompletePreferences
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent
import javax.inject.Inject

@HiltViewModel
class AddEditAutocompleteViewModel @Inject constructor(
    private val repository: AddEditAutocompleteRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditAutocompleteEvent> {

    private val addEditAutocompleteState: MutableState<AddEditAutocomplete> = mutableStateOf(AddEditAutocomplete())

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    val nameState: TextFieldState = TextFieldState()

    private val _cancelState: MutableState<TextData> = mutableStateOf(TextData())
    val cancelState: State<TextData> = _cancelState

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<AddEditAutocompleteScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditAutocompleteScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.AutocompleteUid.key)

    init {
        showCancelButton()
        showSaveButton()
        getAutocomplete()
    }

    override fun onEvent(event: AddEditAutocompleteEvent) {
        when (event) {
            AddEditAutocompleteEvent.SaveAutocomplete -> saveAutocomplete()

            AddEditAutocompleteEvent.CancelSavingAutocomplete -> cancelSavingAutocomplete()

            is AddEditAutocompleteEvent.NameChanged -> nameChanged(event)
        }
    }

    private fun getAutocomplete() = viewModelScope.launch(dispatchers.io) {
        repository.getAddEditAutocomplete(uid).firstOrNull()?.let {
            showAutocomplete(it)
        }
    }

    private fun saveAutocomplete() = viewModelScope.launch(dispatchers.io) {
        if (nameState.isTextEmpty()) {
            nameState.showError(
                error = mapping.toBody(
                    text = mapping.toResourcesUiText(R.string.addEditAutocomplete_nameError),
                    fontSize = FontSize.MEDIUM,
                    appColor = AppColor.Error
                )
            )
            return@launch
        }

        val autocomplete = addEditAutocompleteState.value.autocomplete?.copy(
            name = nameState.currentData.text.text,
            lastModified = System.currentTimeMillis()
        ) ?: Autocomplete(
            name = nameState.currentData.text.text
        )

        if (uid == null) {
            repository.addAutocomplete(autocomplete)
        } else {
            repository.editAutocomplete(autocomplete)
        }

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingAutocomplete() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun nameChanged(event: AddEditAutocompleteEvent.NameChanged) {
        nameState.changeText(event.value)
    }

    private fun showHeader(preferences: AutocompletePreferences) {
        val text: UiText = if (uid == null) {
            mapping.toResourcesUiText(R.string.addEditAutocomplete_header_addAutocomplete)
        } else {
            mapping.toResourcesUiText(R.string.addEditAutocomplete_header_editAutocomplete)
        }

        _headerState.value = mapping.toOnDialogHeader(
            text = text,
            fontSize = preferences.fontSize
        )
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.addEditAutocomplete_cancel),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.addEditAutocomplete_save),
            fontSize = FontSize.MEDIUM
        )
    }

    private suspend fun showAutocomplete(
        addEditAutocomplete: AddEditAutocomplete
    ) = withContext(dispatchers.main) {
        addEditAutocompleteState.value = addEditAutocomplete

        val name = addEditAutocomplete.formatName()
        val preferences = addEditAutocomplete.preferences
        nameState.showTextField(
            text = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditAutocomplete_nameLabel),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = if (preferences.firstLetterUppercase) {
                    KeyboardCapitalization.Sentences
                } else {
                    KeyboardCapitalization.None
                }
            )
        )

        showHeader(preferences)
        showKeyboard()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditAutocompleteScreenEvent.ShowBackScreen)
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}