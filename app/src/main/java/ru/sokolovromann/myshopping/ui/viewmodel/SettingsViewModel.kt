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

            is SettingsEvent.OnNightThemeSelected -> onNightThemeSelected(event)

            is SettingsEvent.OnFontSizeSelected -> onFontSizeSelected(event)

            is SettingsEvent.OnDisplayCompletedSelected -> onDisplayCompletedSelected(event)

            is SettingsEvent.OnAfterSaveProductSelected -> onAfterSaveProductSelected(event)

            is SettingsEvent.OnAfterProductCompletedSelected -> onAfterProductCompletedSelected(event)

            is SettingsEvent.OnAfterShoppingCompletedSelected -> onAfterShoppingCompletedSelected(event)

            is SettingsEvent.OnAfterAddShoppingSelected -> onAfterAddShoppingSelected(event)
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
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.NightTheme
                )
            }

            SettingUid.FontSize -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnShowFontSizes)
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
                _screenEventFlow.emit(SettingsScreenEvent.OnShowDisplayCompleted)
            }

            SettingUid.DisplayEmptyShoppings -> {
                appConfigRepository.invertDisplayEmptyShoppings()
            }

            SettingUid.StrikethroughCompletedProducts -> {
                appConfigRepository.invertStrikethroughCompletedProducts()
            }

            SettingUid.AfterProductCompleted -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.AfterProductCompleted
                )
            }

            SettingUid.AfterShoppingCompleted -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.AfterShoppingCompleted
                )
            }

            SettingUid.DisplayOtherFields -> {
                appConfigRepository.invertDisplayOtherFields()
            }

            SettingUid.DisplayListOfAutocompletes -> {
                appConfigRepository.invertDisplayListOfAutocompletes()
            }

            SettingUid.CompletedWithCheckbox -> {
                appConfigRepository.invertCompletedWithCheckbox()
                _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
            }

            SettingUid.EnterToSaveProducts -> {
                appConfigRepository.invertEnterToSaveProduct()
            }

            SettingUid.AfterSaveProduct -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.AfterSaveProduct
                )
            }

            SettingUid.AfterAddShopping -> {
                settingsState.onSelectUid(
                    expanded = true,
                    settingUid = SettingUid.AfterAddShopping
                )
            }

            SettingUid.AutomaticallyEmptyTrash -> {
                appConfigRepository.invertAutomaticallyEmptyTrash()
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

            SettingUid.SwipeProduct -> {
                _screenEventFlow.emit(SettingsScreenEvent.OnShowSwipeProduct)
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

    private fun onNightThemeSelected(
        event: SettingsEvent.OnNightThemeSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveNightTheme(event.nightTheme)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.NightTheme
        )
        _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
    }

    private fun onFontSizeSelected(
        event: SettingsEvent.OnFontSizeSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveFontSize(event.fontSize, event.fontSize)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.FontSize
        )
        _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
    }

    private fun onDisplayCompletedSelected(
        event: SettingsEvent.OnDisplayCompletedSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.displayCompleted(event.displayCompleted, event.displayCompleted)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.DisplayCompletedPurchases
        )
        _screenEventFlow.emit(SettingsScreenEvent.OnUpdateProductsWidgets)
    }

    private fun onAfterSaveProductSelected(
        event: SettingsEvent.OnAfterSaveProductSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveAfterSaveProduct(event.afterSaveProduct)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.AfterSaveProduct
        )
    }

    private fun onAfterProductCompletedSelected(
        event: SettingsEvent.OnAfterProductCompletedSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveAfterProductCompleted(event.afterProductCompleted)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.AfterProductCompleted
        )
    }

    private fun onAfterShoppingCompletedSelected(
        event: SettingsEvent.OnAfterShoppingCompletedSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveAfterShoppingCompleted(event.afterShoppingCompleted)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.AfterShoppingCompleted
        )
    }

    private fun onAfterAddShoppingSelected(
        event: SettingsEvent.OnAfterAddShoppingSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.saveAfterAddShopping(event.afterAddShopping)
        settingsState.onSelectUid(
            expanded = false,
            settingUid = SettingUid.AfterAddShopping
        )
    }
}