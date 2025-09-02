package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.model.CalculateChangeState
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class CalculateChangeViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CalculateChangeEvent> {

    val calculateChangeState: CalculateChangeState = CalculateChangeState()

    private val _screenEventFlow: MutableSharedFlow<CalculateChangeScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CalculateChangeScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: CalculateChangeEvent) {
        when (event) {
            CalculateChangeEvent.OnClickCancel -> onClickCancel()

            is CalculateChangeEvent.OnUserMoneyChanged -> onUserMoneyChanged(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            calculateChangeState.populate(it)
            _screenEventFlow.emit(CalculateChangeScreenEvent.OnShowKeyboard)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(CalculateChangeScreenEvent.OnShowBackScreen)
    }

    private fun onUserMoneyChanged(event: CalculateChangeEvent.OnUserMoneyChanged) {
        calculateChangeState.onUserMoneyValueChanged(event.value)
    }
}