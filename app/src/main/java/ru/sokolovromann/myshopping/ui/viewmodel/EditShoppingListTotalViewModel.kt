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
import ru.sokolovromann.myshopping.data.repository.EditShoppingListTotalRepository
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListTotalScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.EditShoppingListTotalState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListTotalEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListTotalViewModel @Inject constructor(
    private val repository: EditShoppingListTotalRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListTotalEvent> {

    val editShoppingListTotalState: EditShoppingListTotalState = EditShoppingListTotalState()

    private val _screenEventFlow: MutableSharedFlow<EditShoppingListTotalScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditShoppingListTotalScreenEvent> = _screenEventFlow

    init {
        getEditShoppingListTotal()
    }

    override fun onEvent(event: EditShoppingListTotalEvent) {
        when (event) {
            EditShoppingListTotalEvent.SaveShoppingListTotal -> saveShoppingListTotal()

            EditShoppingListTotalEvent.CancelSavingShoppingListTotal -> cancelSavingShoppingListTotal()

            is EditShoppingListTotalEvent.ShoppingListTotalChanged -> shoppingListTotalChanged(event)
        }
    }

    private fun getEditShoppingListTotal() = viewModelScope.launch {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        repository.getEditShoppingListTotal(uid).firstOrNull()?.let {
            editShoppingListTotalLoaded(it)
        }
    }

    private suspend fun editShoppingListTotalLoaded(
        editShoppingListTotal: EditShoppingListTotal
    ) = withContext(dispatchers.main) {
        editShoppingListTotalState.populate(editShoppingListTotal)
        _screenEventFlow.emit(EditShoppingListTotalScreenEvent.ShowKeyboard)
    }

    private fun saveShoppingListTotal() = viewModelScope.launch {
        val shoppingList = editShoppingListTotalState.getShoppingListResult()
            .getOrElse { return@launch }

        if (shoppingList.totalFormatted) {
            repository.saveShoppingListTotal(
                uid = shoppingList.uid,
                total = shoppingList.total,
                lastModified = shoppingList.lastModified
            )
        } else {
            repository.deleteShoppingListTotal(
                uid = shoppingList.uid,
                lastModified = shoppingList.lastModified
            )
        }

        withContext(dispatchers.main) {
            _screenEventFlow.emit(EditShoppingListTotalScreenEvent.ShowBackScreen)
        }
    }

    private fun cancelSavingShoppingListTotal() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(EditShoppingListTotalScreenEvent.ShowBackScreen)
    }

    private fun shoppingListTotalChanged(event: EditShoppingListTotalEvent.ShoppingListTotalChanged) {
        editShoppingListTotalState.changeTotalValue(event.value)
    }
}