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
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.CalculateChangeState
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent
import javax.inject.Inject

@HiltViewModel
class CalculateChangeViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CalculateChangeEvent> {

    val calculateChangeState: CalculateChangeState = CalculateChangeState()

    private val _screenEventFlow: MutableSharedFlow<CalculateChangeScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CalculateChangeScreenEvent> = _screenEventFlow

    init {
        getCalculateChange()
    }

    override fun onEvent(event: CalculateChangeEvent) {
        when (event) {
            CalculateChangeEvent.ShowBackScreen -> showBackScreen()

            is CalculateChangeEvent.UserMoneyChanged -> userMoneyChange(event)
        }
    }

    private fun getCalculateChange() = viewModelScope.launch {
        val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        shoppingListsRepository.getShoppingListWithConfig(uid).firstOrNull()?.let {
            shoppingListLoaded(it)
        }
    }

    private suspend fun shoppingListLoaded(
        shoppingListWithConfig: ShoppingListWithConfig
    ) = withContext(AppDispatchers.Main) {
        calculateChangeState.populate(shoppingListWithConfig)
        _screenEventFlow.emit(CalculateChangeScreenEvent.ShowKeyboard)
    }

    private fun userMoneyChange(event: CalculateChangeEvent.UserMoneyChanged) {
        calculateChangeState.changeUserMoneyValue(event.value)
    }

    private fun showBackScreen() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(CalculateChangeScreenEvent.ShowBackScreen)
    }
}