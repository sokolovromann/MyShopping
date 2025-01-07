package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
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

    init { onInit() }

    override fun onEvent(event: EditShoppingListNameEvent) {
        when (event) {
            EditShoppingListNameEvent.OnClickSave -> onClickSave()

            EditShoppingListNameEvent.OnClickCancel -> onClickCancel()

            is EditShoppingListNameEvent.OnNameChanged -> onNameChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            val isFromPurchases = savedStateHandle.get<Boolean>(UiRouteKey.IsFromPurchases.key)
            editShoppingListNameState.populate(it, isFromPurchases)
            _screenEventFlow.emit(EditShoppingListNameScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        editShoppingListNameState.onWaiting()

        val shopping = editShoppingListNameState.getCurrentShopping()
        shoppingListsRepository.saveShoppingListName(
            shoppingUid = shopping.uid,
            name = shopping.name
        )

        val event = if (editShoppingListNameState.isFromPurchases) {
            EditShoppingListNameScreenEvent.OnShowProductsScreen(shopping.uid)
        } else {
            EditShoppingListNameScreenEvent.OnShowBackScreen(shopping.uid)
        }
        _screenEventFlow.emit(event)
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        val uid = editShoppingListNameState.getCurrentShopping().uid
        _screenEventFlow.emit(EditShoppingListNameScreenEvent.OnShowBackScreen(uid))
    }

    private fun onNameChanged(event: EditShoppingListNameEvent.OnNameChanged) {
        editShoppingListNameState.onNameValueChanged(event.value)
    }
}