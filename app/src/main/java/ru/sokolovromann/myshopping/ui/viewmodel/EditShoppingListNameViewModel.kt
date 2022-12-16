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
import ru.sokolovromann.myshopping.data.repository.EditShoppingListNameRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.compose.state.TextFieldState
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListNameViewModel @Inject constructor(
    private val repository: EditShoppingListNameRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListNameEvent> {

    private val editShoppingListNameState: MutableState<EditShoppingListName> = mutableStateOf(
        EditShoppingListName()
    )

    private val _headerState: MutableState<TextData> = mutableStateOf(TextData())
    val headerState: State<TextData> = _headerState

    val nameState: TextFieldState = TextFieldState()

    private val _cancelState: MutableState<TextData> = mutableStateOf(TextData())
    val cancelState: State<TextData> = _cancelState

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<EditShoppingListNameScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditShoppingListNameScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)

    init {
        showCancelButton()
        showSaveButton()
        getShoppingListName()
    }

    override fun onEvent(event: EditShoppingListNameEvent) {
        when (event) {
            EditShoppingListNameEvent.SaveShoppingListName -> saveShoppingListName()

            EditShoppingListNameEvent.CancelSavingShoppingListName -> cancelSavingShoppingListName()

            is EditShoppingListNameEvent.ShoppingListNameChanged -> shoppingListNameChanged(event)
        }
    }

    private fun getShoppingListName() = viewModelScope.launch(dispatchers.io) {
        repository.getEditShoppingListName(uid).firstOrNull()?.let {
            showEditShoppingListName(it)
        }
    }

    private fun saveShoppingListName() = viewModelScope.launch(dispatchers.io) {
        repository.saveShoppingListName(
            uid = uid ?: "",
            name = mapping.toString(nameState.currentData.text),
            lastModified = System.currentTimeMillis()
        )

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingShoppingListName() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun shoppingListNameChanged(event: EditShoppingListNameEvent.ShoppingListNameChanged) {
        nameState.changeText(event.value)
    }

    private fun showHeader(isAdd: Boolean, preferences: ProductPreferences) {
        val text: UiText = if (isAdd) {
            mapping.toResourcesUiText(R.string.editShoppingListName_header_addShoppingListName)
        } else {
            mapping.toResourcesUiText(R.string.editShoppingListName_header_editShoppingListName)
        }

        _headerState.value = mapping.toOnDialogHeader(
            text = text,
            fontSize = preferences.fontSize
        )
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editShoppingListName_cancel),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editShoppingListName_save),
            fontSize = FontSize.MEDIUM
        )
    }

    private suspend fun showEditShoppingListName(
        editShoppingListName: EditShoppingListName
    ) = withContext(dispatchers.main) {
        editShoppingListNameState.value = editShoppingListName

        val name = editShoppingListName.formatName()
        val preferences = editShoppingListName.preferences
        nameState.showTextField(
            text = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.editShoppingListName_nameLabel),
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

        showHeader(name.isEmpty(), preferences)
        showKeyboard()
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.ShowBackScreen)
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}