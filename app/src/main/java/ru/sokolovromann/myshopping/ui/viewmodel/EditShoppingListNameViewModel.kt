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
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditShoppingListNameState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListNameViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
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
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            shoppingListLoaded(it)
        }
    }

    private suspend fun shoppingListLoaded(
        shoppingListWithConfig: ShoppingListWithConfig
    ) = withContext(AppDispatchers.Main) {
        editShoppingListNameState.populate(shoppingListWithConfig)
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.ShowKeyboard)
    }

    private fun saveShoppingListName() = viewModelScope.launch {
        editShoppingListNameState.onWaiting()

        val shopping = editShoppingListNameState.getCurrentShopping()
        shoppingListsRepository.saveShoppingListName(
            shoppingUid = shopping.uid,
            name = shopping.name
        )

        withContext(AppDispatchers.Main) {
            val event = EditShoppingListNameScreenEvent.ShowBackScreenAndUpdateProductsWidget(shopping.uid)
            _screenEventFlow.emit(event)
        }
    }

    private fun cancelSavingShoppingListName() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.ShowBackScreen)
    }

    private fun shoppingListNameChanged(event: EditShoppingListNameEvent.ShoppingListNameChanged) {
        editShoppingListNameState.onNameValueChanged(event.value)
    }
}