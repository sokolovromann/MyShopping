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
import ru.sokolovromann.myshopping.data.repository.CalculateChangeRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.CalculateChangeScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.CalculateChangeState
import ru.sokolovromann.myshopping.ui.viewmodel.event.CalculateChangeEvent
import javax.inject.Inject

@HiltViewModel
class CalculateChangeViewModel @Inject constructor(
    private val repository: CalculateChangeRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<CalculateChangeEvent> {

    val calculateChangeState: CalculateChangeState = CalculateChangeState()

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<CalculateChangeScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<CalculateChangeScreenEvent> = _screenEventFlow

    private val uid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)

    init {
        getCalculateChange()
    }

    override fun onEvent(event: CalculateChangeEvent) {
        when (event) {
            CalculateChangeEvent.ShowBackScreen -> showBackScreen()

            is CalculateChangeEvent.UserMoneyChanged -> userMoneyChange(event)
        }
    }

    private fun getCalculateChange() = viewModelScope.launch(dispatchers.io) {
        repository.getCalculateChange(uid).firstOrNull()?.let {
            showCalculateChange(it)
        }
    }

    private suspend fun showCalculateChange(
        calculateChange: CalculateChange
    ) = withContext(dispatchers.main) {
        calculateChangeState.populate(calculateChange)
        showKeyboard()
    }

    private fun userMoneyChange(event: CalculateChangeEvent.UserMoneyChanged) {
        calculateChangeState.changeUserMoneyValue(event.value)
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(CalculateChangeScreenEvent.ShowBackScreen)
        hideKeyboard()
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }
}