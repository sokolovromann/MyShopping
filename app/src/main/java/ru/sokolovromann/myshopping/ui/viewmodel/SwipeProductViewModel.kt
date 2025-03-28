package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.SwipeProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.SwipeProductState
import ru.sokolovromann.myshopping.ui.viewmodel.event.SwipeProductEvent
import javax.inject.Inject

@HiltViewModel
class SwipeProductViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<SwipeProductEvent> {

    val swipeProductState = SwipeProductState()

    private val _screenEventFlow: MutableSharedFlow<SwipeProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<SwipeProductScreenEvent> = _screenEventFlow

    init {
        onInit()
    }

    override fun onEvent(event: SwipeProductEvent) {
        when (event) {
            SwipeProductEvent.OnClickCancel -> onClickCancel()
            SwipeProductEvent.OnClickSave -> onClickSave()
            is SwipeProductEvent.OnSelectSwipeProductLeft -> onSelectSwipeProductLeft(event)
            is SwipeProductEvent.OnSwipeProductLeftSelected -> onSwipeProductLeftSelected(event)
            is SwipeProductEvent.OnSelectSwipeProductRight -> onSelectSwipeProductRight(event)
            is SwipeProductEvent.OnSwipeProductRightSelected -> onSwipeProductRightSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        swipeProductState.onWaiting()

        appConfigRepository.getAppConfig().collect {
            swipeProductState.populate(it)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SwipeProductScreenEvent.OnShowBackScreen)
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        swipeProductState.onWaiting()

        appConfigRepository.saveSwipeProduct(
            left = swipeProductState.swipeProductLeftValue.selected,
            right = swipeProductState.swipeProductRightValue.selected
        ).onSuccess { _screenEventFlow.emit(SwipeProductScreenEvent.OnShowBackScreen) }
    }

    private fun onSelectSwipeProductLeft(event: SwipeProductEvent.OnSelectSwipeProductLeft) {
        swipeProductState.onSelectSwipeProductLeft(event.expanded)
    }

    private fun onSwipeProductLeftSelected(event: SwipeProductEvent.OnSwipeProductLeftSelected) {
        swipeProductState.onSwipeProductLeftSelected(event.swipeProduct)
    }

    private fun onSelectSwipeProductRight(event: SwipeProductEvent.OnSelectSwipeProductRight) {
        swipeProductState.onSelectSwipeProductRight(event.expanded)
    }

    private fun onSwipeProductRightSelected(event: SwipeProductEvent.OnSwipeProductRightSelected) {
        swipeProductState.onSwipeProductRightSelected(event.swipeProduct)
    }
}