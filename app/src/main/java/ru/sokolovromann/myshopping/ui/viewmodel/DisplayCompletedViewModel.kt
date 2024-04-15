package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.DisplayCompletedScreenEvent
import ru.sokolovromann.myshopping.ui.model.DisplayCompletedState
import ru.sokolovromann.myshopping.ui.viewmodel.event.DisplayCompletedEvent
import javax.inject.Inject

@HiltViewModel
class DisplayCompletedViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<DisplayCompletedEvent> {

    val displayCompletedState: DisplayCompletedState = DisplayCompletedState()

    private val _screenEventFlow: MutableSharedFlow<DisplayCompletedScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<DisplayCompletedScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: DisplayCompletedEvent) {
        when (event) {
            DisplayCompletedEvent.OnClickSave -> onClickSave()
            DisplayCompletedEvent.OnClickCancel -> onClickCancel()
            is DisplayCompletedEvent.OnSelectAppDisplayCompleted -> onSelectAppDisplayCompleted(event)
            is DisplayCompletedEvent.OnAppDisplayCompletedSelected -> onAppDisplayCompletedSelected(event)
            is DisplayCompletedEvent.OnSelectWidgetDisplayCompleted -> onSelectWidgetDisplayCompleted(event)
            is DisplayCompletedEvent.OnWidgetDisplayCompletedSelected -> onWidgetDisplayCompletedSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        displayCompletedState.onWaiting()

        appConfigRepository.getAppConfig().collect {
            displayCompletedState.populate(it)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(DisplayCompletedScreenEvent.OnShowBackScreen)
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        displayCompletedState.onWaiting()

        appConfigRepository.displayCompleted(
            appDisplayCompleted = displayCompletedState.appDisplayCompletedValue.selected,
            widgetDisplayCompleted = displayCompletedState.widgetDisplayCompletedValue.selected,
        ).onSuccess { _screenEventFlow.emit(DisplayCompletedScreenEvent.OnShowBackScreen) }
    }

    private fun onSelectAppDisplayCompleted(event: DisplayCompletedEvent.OnSelectAppDisplayCompleted) {
        displayCompletedState.onSelectAppDisplayCompleted(event.expanded)
    }

    private fun onAppDisplayCompletedSelected(event: DisplayCompletedEvent.OnAppDisplayCompletedSelected) {
        displayCompletedState.onAppDisplayCompletedSelected(event.displayCompleted)
    }

    private fun onSelectWidgetDisplayCompleted(event: DisplayCompletedEvent.OnSelectWidgetDisplayCompleted) {
        displayCompletedState.onSelectWidgetDisplayCompleted(event.expanded)
    }

    private fun onWidgetDisplayCompletedSelected(event: DisplayCompletedEvent.OnWidgetDisplayCompletedSelected) {
        displayCompletedState.onWidgetDisplayCompletedSelected(event.displayCompleted)
    }
}