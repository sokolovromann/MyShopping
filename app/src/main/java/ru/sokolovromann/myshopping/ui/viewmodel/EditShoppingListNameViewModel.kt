package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import ru.sokolovromann.myshopping.ui.compose.state.EditShoppingListNameState
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListNameViewModel @Inject constructor(
    private val repository: EditShoppingListNameRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListNameEvent> {

    val editShoppingListNameState: EditShoppingListNameState = EditShoppingListNameState()

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
        val shoppingList = editShoppingListNameState.getShoppingListResult()
            .getOrElse { return@launch }

        repository.saveShoppingListName(
            uid = shoppingList.uid,
            name = shoppingList.name,
            lastModified = shoppingList.lastModified
        )

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingShoppingListName() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun shoppingListNameChanged(event: EditShoppingListNameEvent.ShoppingListNameChanged) {
        editShoppingListNameState.changeNameValue(event.value)
    }

    private fun showCancelButton() {
        _cancelState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editShoppingListName_action_cancelSavingShoppingListName),
            fontSize = FontSize.MEDIUM
        )
    }

    private fun showSaveButton() {
        _saveState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.editShoppingListName_action_saveShoppingListName),
            fontSize = FontSize.MEDIUM
        )
    }

    private suspend fun showEditShoppingListName(
        editShoppingListName: EditShoppingListName
    ) = withContext(dispatchers.main) {
        editShoppingListNameState.populate(editShoppingListName)
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