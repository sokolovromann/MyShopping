package ru.sokolovromann.myshopping.ui.viewmodel

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
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditShoppingListNameState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListNameViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListNameEvent> {

    val editShoppingListNameState: EditShoppingListNameState = EditShoppingListNameState()

    private val _screenEventFlow: MutableSharedFlow<EditShoppingListNameScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditShoppingListNameScreenEvent> = _screenEventFlow

    init {
        getEditShoppingListName()
    }

    override fun onEvent(event: EditShoppingListNameEvent) {
        when (event) {
            EditShoppingListNameEvent.SaveShoppingListName -> saveShoppingListName()

            EditShoppingListNameEvent.CancelSavingShoppingListName -> cancelSavingShoppingListName()

            is EditShoppingListNameEvent.ShoppingListNameChanged -> shoppingListNameChanged(event)
        }
    }

    private fun getEditShoppingListName() = viewModelScope.launch {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getEditShoppingListName(uid).firstOrNull()?.let {
            editShoppingListNameLoaded(it)
        }
    }

    private suspend fun editShoppingListNameLoaded(
        editShoppingListName: EditShoppingListName
    ) = withContext(dispatchers.main) {
        editShoppingListNameState.populate(editShoppingListName)
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.ShowKeyboard)
    }

    private fun saveShoppingListName() = viewModelScope.launch {
        val shoppingList = editShoppingListNameState.getShoppingListResult()
            .getOrElse { return@launch }

        shoppingListsRepository.saveShoppingListName(
            uid = shoppingList.uid,
            name = shoppingList.name,
            lastModified = shoppingList.lastModified
        )

        withContext(dispatchers.main) {
            val event = EditShoppingListNameScreenEvent.ShowBackScreenAndUpdateProductsWidget(shoppingList.uid)
            _screenEventFlow.emit(event)
        }
    }

    private fun cancelSavingShoppingListName() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.ShowBackScreen)
    }

    private fun shoppingListNameChanged(event: EditShoppingListNameEvent.ShoppingListNameChanged) {
        editShoppingListNameState.changeNameValue(event.value)
    }
}