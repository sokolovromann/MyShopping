package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.FontSizesScreenEvent
import ru.sokolovromann.myshopping.ui.model.FontSizesState
import ru.sokolovromann.myshopping.ui.viewmodel.event.FontSizesEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class FontSizesViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<FontSizesEvent> {

    val fontSizesState = FontSizesState()

    private val _screenEventFlow: MutableSharedFlow<FontSizesScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<FontSizesScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: FontSizesEvent) {
        when (event) {
            FontSizesEvent.OnClickSave -> onClickSave()
            FontSizesEvent.OnClickCancel -> onClickCancel()
            is FontSizesEvent.OnSelectAppFontSize -> onSelectAppFontSize(event)
            is FontSizesEvent.OnAppFontSizeSelected -> onAppFontSizeSelected(event)
            is FontSizesEvent.OnSelectWidgetFontSize -> onSelectWidgetFontSize(event)
            is FontSizesEvent.OnWidgetFontSizeSelected -> onWidgetFontSizeSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        fontSizesState.onWaiting()

        appConfigRepository.getAppConfig().collect {
            fontSizesState.populate(it)
        }
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(FontSizesScreenEvent.OnShowBackScreen)
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        fontSizesState.onWaiting()

        appConfigRepository.saveFontSize(
            appFontSize = fontSizesState.appFontSizeValue.selected,
            widgetFontSize = fontSizesState.widgetFontSizeValue.selected
        ).onSuccess { _screenEventFlow.emit(FontSizesScreenEvent.OnShowBackScreen) }
    }

    private fun onSelectAppFontSize(event: FontSizesEvent.OnSelectAppFontSize) {
        fontSizesState.onSelectAppFontSize(event.expanded)
    }

    private fun onAppFontSizeSelected(event: FontSizesEvent.OnAppFontSizeSelected) {
        fontSizesState.onAppFontSizeSelected(event.fontSize)
    }

    private fun onSelectWidgetFontSize(event: FontSizesEvent.OnSelectWidgetFontSize) {
        fontSizesState.onSelectWidgetFontSize(event.expanded)
    }

    private fun onWidgetFontSizeSelected(event: FontSizesEvent.OnWidgetFontSizeSelected) {
        fontSizesState.onWidgetFontSizeSelected(event.fontSize)
    }
}