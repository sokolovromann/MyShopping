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
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListTotalScreenEvent
import ru.sokolovromann.myshopping.ui.model.EditShoppingListTotalState
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListTotalEvent
import javax.inject.Inject

@HiltViewModel
class EditShoppingListTotalViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelEvent<EditShoppingListTotalEvent> {

    val editShoppingListTotalState: EditShoppingListTotalState = EditShoppingListTotalState()

    private val _screenEventFlow: MutableSharedFlow<EditShoppingListTotalScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<EditShoppingListTotalScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: EditShoppingListTotalEvent) {
        when (event) {
            EditShoppingListTotalEvent.OnClickSave -> onClickSave()

            EditShoppingListTotalEvent.OnClickCancel -> onClickCancel()

            is EditShoppingListTotalEvent.OnTotalChanged -> onTotalChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            editShoppingListTotalState.populate(it)
            _screenEventFlow.emit(EditShoppingListTotalScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        editShoppingListTotalState.onWaiting()

        val shopping = editShoppingListTotalState.getCurrentShopping()

        if (shopping.totalFormatted) {
            shoppingListsRepository.saveShoppingListTotal(
                shoppingUid = shopping.uid,
                total = shopping.total
            )
        } else {
            shoppingListsRepository.deleteShoppingListTotal(shopping.uid)
        }

        val event = EditShoppingListTotalScreenEvent.OnShowBackScreen(shopping.uid)
        _screenEventFlow.emit(event)
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        val uid = editShoppingListTotalState.getCurrentShopping().uid
        _screenEventFlow.emit(EditShoppingListTotalScreenEvent.OnShowBackScreen(uid))
    }

    private fun onTotalChanged(event: EditShoppingListTotalEvent.OnTotalChanged) {
        editShoppingListTotalState.onTotalValueChanged(event.value)
    }
}