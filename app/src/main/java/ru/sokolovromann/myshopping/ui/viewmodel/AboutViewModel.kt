package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.AboutScreenEvent
import ru.sokolovromann.myshopping.ui.model.AboutState
import ru.sokolovromann.myshopping.ui.viewmodel.event.AboutEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<AboutEvent> {

    val aboutState: AboutState = AboutState()

    private val _screenEventFlow: MutableSharedFlow<AboutScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AboutScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: AboutEvent) {
        when(event) {
            AboutEvent.OnClickBack -> onClickBack()

            AboutEvent.OnClickEmail -> onClickEmail()

            AboutEvent.OnClickGitHub -> onClickGitHub()

            AboutEvent.OnClickPrivacyPolicy -> onClickPrivacyPolicy()

            AboutEvent.OnClickTermsAndConditions -> onClickTermsAndConditions()

            is AboutEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is AboutEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        aboutState.onWaiting()

        appConfigRepository.getSettingsWithConfig().collect {
            aboutState.populate(it)
        }
    }

    private fun onClickBack() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnShowBackScreen)
    }

    private fun onClickEmail() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnSendEmailToDeveloper)
    }

    private fun onClickGitHub() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnShowAppGithub)
    }

    private fun onClickPrivacyPolicy() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnShowPrivacyPolicy)
    }

    private fun onClickTermsAndConditions() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnShowTermsAndConditions)
    }

    private fun onDrawerScreenSelected(
        event: AboutEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: AboutEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(AboutScreenEvent.OnSelectDrawerScreen(event.display))
    }
}