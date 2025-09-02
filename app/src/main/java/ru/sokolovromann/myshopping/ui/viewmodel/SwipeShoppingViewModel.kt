package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.SwipeShoppingScreenEvent
import ru.sokolovromann.myshopping.ui.model.SwipeShoppingState
import ru.sokolovromann.myshopping.ui.viewmodel.event.SwipeShoppingEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class SwipeShoppingViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<SwipeShoppingEvent> {

    val swipeShoppingState = SwipeShoppingState()

    private val _screenEventFlow: MutableSharedFlow<SwipeShoppingScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<SwipeShoppingScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init {
        onInit()
    }

    override fun onEvent(event: SwipeShoppingEvent) {
        when (event) {
            SwipeShoppingEvent.OnClickCancel -> onClickCancel()
            SwipeShoppingEvent.OnClickSave -> onClickSave()
            is SwipeShoppingEvent.OnSelectSwipeShoppingLeft -> onSelectSwipeShoppingLeft(event)
            is SwipeShoppingEvent.OnSelectSwipeShoppingRight -> onSelectSwipeShoppingRight(event)
            is SwipeShoppingEvent.OnSwipeShoppingLeftSelected -> onSwipeShoppingLeftSelected(event)
            is SwipeShoppingEvent.OnSwipeShoppingRightSelected -> onSwipeShoppingRightSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        swipeShoppingState.onWaiting()

        appConfigRepository.getAppConfig().collect {
            swipeShoppingState.populate(it)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(SwipeShoppingScreenEvent.OnShowBackScreen)
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        swipeShoppingState.onWaiting()

        appConfigRepository.saveSwipeShopping(
            left = swipeShoppingState.swipeShoppingLeftValue.selected,
            right = swipeShoppingState.swipeShoppingRightValue.selected
        ).onSuccess { _screenEventFlow.emit(SwipeShoppingScreenEvent.OnShowBackScreen) }
    }

    private fun onSelectSwipeShoppingLeft(event: SwipeShoppingEvent.OnSelectSwipeShoppingLeft) {
        swipeShoppingState.onSelectSwipeShoppingLeft(event.expanded)
    }

    private fun onSwipeShoppingLeftSelected(event: SwipeShoppingEvent.OnSwipeShoppingLeftSelected) {
        swipeShoppingState.onSwipeShoppingLeftSelected(event.swipeShopping)
    }

    private fun onSelectSwipeShoppingRight(event: SwipeShoppingEvent.OnSelectSwipeShoppingRight) {
        swipeShoppingState.onSelectSwipeShoppingRight(event.expanded)
    }

    private fun onSwipeShoppingRightSelected(event: SwipeShoppingEvent.OnSwipeShoppingRightSelected) {
        swipeShoppingState.onSwipeShoppingRightSelected(event.swipeShopping)
    }
}