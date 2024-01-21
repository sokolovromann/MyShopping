package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.model.SettingUid
import ru.sokolovromann.myshopping.ui.model.SettingsState
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel(), ViewModelEvent<SettingsEvent> {

    val settingsState: SettingsState = SettingsState()

    private val _screenEventFlow: MutableSharedFlow<SettingsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<SettingsScreenEvent> = _screenEventFlow

    init { onInit() }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnClickBack -> onClickBack()

            is SettingsEvent.OnSettingItemSelected -> onSettingItemSelected(event)

            is SettingsEvent.OnSelectSettingItem -> onSelectSettingItem(event)

            is SettingsEvent.OnDrawerScreenSelected -> onDrawerScreenSelected(event)

            is SettingsEvent.OnSelectDrawerScreen -> onSelectDrawerScreen(event)

            is SettingsEvent.OnFontSizeSelected -> onFontSizeSelected(event)

            is SettingsEvent.OnDisplayCompletedSelected -> onDisplayCompletedSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        settingsState.onWaiting()

        appConfigRepository.getSettingsWithConfig().collect {
            settingsState.populate(it)
        }
    }

    private fun onClickBack() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.OnShowBackScreen)
    }

    private fun onSettingItemSelected(
        event: SettingsEvent.OnSettingItemSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        when (event.uid) {
            SettingUid.NightTheme -> {
                appConfigRepository.invertNightTheme()
            }

            SettingUid.FontSize -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.FontSize
                )
            }

            SettingUid.Backup -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnShowBackupScreen)
            }

            SettingUid.DisplayMoney -> {
                appConfigRepository.invertDisplayMoney()
                _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
            }

            SettingUid.Currency -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnEditCurrencyScreen)
            }

            SettingUid.DisplayCurrencyToLeft -> {
                appConfigRepository.invertDisplayCurrencyToLeft()
                _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
            }

            SettingUid.DisplayMoneyZeros -> {
                appConfigRepository.invertDisplayMoneyZeros()
            }

            SettingUid.TaxRate -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnEditTaxRateScreen)
            }

            SettingUid.DisplayDefaultAutocomplete -> {
                appConfigRepository.invertDisplayDefaultAutocompletes()
            }

            SettingUid.DisplayCompletedPurchases -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.DisplayCompletedPurchases
                )
            }

            SettingUid.DisplayOtherFields -> {
                appConfigRepository.invertDisplayOtherFields()
            }

            SettingUid.EditProductAfterCompleted -> {
                appConfigRepository.invertEditProductAfterCompleted()
            }

            SettingUid.CompletedWithCheckbox -> {
                appConfigRepository.invertCompletedWithCheckbox()
                _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
            }

            SettingUid.EnterToSaveProducts -> {
                appConfigRepository.invertEnterToSaveProduct()
            }

            SettingUid.ColoredCheckbox -> {
                appConfigRepository.invertColoredCheckbox()
                _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
            }

            SettingUid.SaveProductToAutocompletes -> {
                appConfigRepository.invertSaveProductToAutocompletes()
            }

            SettingUid.MaxAutocompletes -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnShowMaxAutocompletes)
            }

            SettingUid.Developer -> {}

            SettingUid.Email -> {
                val email = settingsState.getSettings().developerEmail
                _screenEventFlow.emit(SettingsScreenEvent.OnSendEmailToDeveloper(email))
            }

            SettingUid.AppVersion -> {}

            SettingUid.Github -> {
                val link = settingsState.getSettings().appGithubLink
                _screenEventFlow.emit(SettingsScreenEvent.OnShowAppGithub(link))
            }

            SettingUid.PrivacyPolicy -> {
                val link = settingsState.getSettings().privacyPolicyLink
                _screenEventFlow.emit(SettingsScreenEvent.OnShowPrivacyPolicy(link))
            }

            SettingUid.TermsAndConditions -> {
                val link = settingsState.getSettings().termsAndConditionsLink
                _screenEventFlow.emit(SettingsScreenEvent.OnShowTermsAndConditions(link))
            }
        }
    }

    private fun onSelectSettingItem(event: SettingsEvent.OnSelectSettingItem) {
        settingsState.onSelectUid(event.expanded, event.uid)
    }

    private fun onDrawerScreenSelected(
        event: SettingsEvent.OnDrawerScreenSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.OnDrawerScreenSelected(event.drawerScreen))
    }

    private fun onSelectDrawerScreen(
        event: SettingsEvent.OnSelectDrawerScreen
    ) = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.OnSelectDrawerScreen(event.display))
    }

    private fun onFontSizeSelected(
        event: SettingsEvent.OnFontSizeSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveFontSize(event.fontSize)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.FontSize
        )
        _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
    }

    private fun onDisplayCompletedSelected(
        event: SettingsEvent.OnDisplayCompletedSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.displayCompleted(event.displayCompleted)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.DisplayCompletedPurchases
        )
        _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
    }
}