package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.UiRoute
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

    init {
        getSettings()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SelectSettingsItem -> selectSettingsItem(event)

            is SettingsEvent.SelectNavigationItem -> selectNavigationItem(event)

            SettingsEvent.SelectDisplayCompletedPurchases -> selectDisplayCompletedPurchases()

            is SettingsEvent.FontSizeSelected -> fontSizeSelected(event)

            is SettingsEvent.DisplayCompletedPurchasesSelected -> displayCompletedPurchasesSelected(event)

            is SettingsEvent.DisplayShoppingsProductsSelected -> displayShoppingsProductsSelected(event)

            SettingsEvent.ShowBackScreen -> showBackScreen()

            SettingsEvent.ShowNavigationDrawer -> showNavigationDrawer()

            SettingsEvent.HideFontSize -> hideFontSize()

            SettingsEvent.HideNavigationDrawer -> hideNavigationDrawer()

            SettingsEvent.HideDisplayCompletedPurchases -> hideDisplayCompletedPurchases()

            SettingsEvent.HideDisplayShoppingsProducts -> hideDisplayShoppingsProducts()
        }
    }

    private fun getSettings() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            settingsState.onWaiting()
        }

        appConfigRepository.getSettingsWithConfig().collect {
            settingsLoaded(it)
        }
    }

    private suspend fun settingsLoaded(
        settingsWithConfig: SettingsWithConfig
    ) = withContext(AppDispatchers.Main) {
        settingsState.populate(settingsWithConfig)
    }

    private fun editCurrencySymbol() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.EditCurrency)
    }

    private fun editTaxRate() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.EditTaxRate)
    }

    private fun selectSettingsItem(event: SettingsEvent.SelectSettingsItem) {
        when (event.uid) {
            SettingUid.NightTheme -> invertNightTheme()

            SettingUid.FontSize -> selectFontSize()

            SettingUid.Backup -> backup()

            SettingUid.DisplayMoney -> invertDisplayMoney()

            SettingUid.Currency -> editCurrencySymbol()

            SettingUid.DisplayCurrencyToLeft -> invertDisplayCurrencyToLeft()

            SettingUid.DisplayMoneyZeros -> invertDisplayMoneyZeros()

            SettingUid.TaxRate -> editTaxRate()

            SettingUid.DisplayDefaultAutocomplete -> invertDisplayDefaultAutocomplete()

            SettingUid.DisplayCompletedPurchases -> selectDisplayCompletedPurchases()

            SettingUid.DisplayOtherFields -> invertDisplayOtherFields()

            SettingUid.EditProductAfterCompleted -> invertEditProductAfterCompleted()

            SettingUid.CompletedWithCheckbox -> invertCompletedWithCheckbox()

            SettingUid.DisplayShoppingsProducts -> selectShoppingsProducts()

            SettingUid.EnterToSaveProducts -> enterToSaveProducts()

            SettingUid.ColoredCheckbox -> invertColoredCheckbox()

            SettingUid.SaveProductToAutocompletes -> invertSaveProductToAutocompletes()

            SettingUid.Developer -> return

            SettingUid.Email -> sendEmailToDeveloper()

            SettingUid.AppVersion -> return

            SettingUid.Github -> showAppGithub()

            SettingUid.PrivacyPolicy -> showPrivacyPolicy()

            SettingUid.TermsAndConditions -> showTermsAndConditions()
        }
    }

    private fun selectFontSize() {
        settingsState.onSelectUid(true, SettingUid.FontSize)
    }

    private fun backup() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackup)
    }

    private fun selectShoppingsProducts() {
        settingsState.onSelectUid(true, SettingUid.DisplayShoppingsProducts)
    }

    private fun selectNavigationItem(
        event: SettingsEvent.SelectNavigationItem
    ) = viewModelScope.launch(AppDispatchers.Main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(SettingsScreenEvent.ShowPurchases)
            UiRoute.Archive -> _screenEventFlow.emit(SettingsScreenEvent.ShowArchive)
            UiRoute.Trash -> _screenEventFlow.emit(SettingsScreenEvent.ShowTrash)
            UiRoute.Autocompletes -> _screenEventFlow.emit(SettingsScreenEvent.ShowAutocompletes)
            else -> return@launch
        }
    }

    private fun selectDisplayCompletedPurchases() {
        settingsState.onSelectUid(true, SettingUid.DisplayCompletedPurchases)
    }

    private fun fontSizeSelected(
        event: SettingsEvent.FontSizeSelected
    ) = viewModelScope.launch {
        appConfigRepository.saveFontSize(event.fontSize)

        withContext(AppDispatchers.Main) {
            hideFontSize()
            _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
        }
    }

    private fun displayCompletedPurchasesSelected(
        event: SettingsEvent.DisplayCompletedPurchasesSelected
    ) = viewModelScope.launch {
        appConfigRepository.displayCompleted(event.displayCompleted)

        withContext(AppDispatchers.Main) {
            hideDisplayCompletedPurchases()
            _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
        }
    }

    private fun displayShoppingsProductsSelected(
        event: SettingsEvent.DisplayShoppingsProductsSelected
    ) = viewModelScope.launch {
        appConfigRepository.displayShoppingsProducts(event.displayProducts)
    }

    private fun invertNightTheme() = viewModelScope.launch {
        appConfigRepository.invertNightTheme()
    }

    private fun invertDisplayMoney() = viewModelScope.launch {
        appConfigRepository.invertDisplayMoney()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertDisplayCurrencyToLeft() = viewModelScope.launch {
        appConfigRepository.invertDisplayCurrencyToLeft()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertDisplayMoneyZeros() = viewModelScope.launch {
        appConfigRepository.invertDisplayMoneyZeros()
    }

    private fun invertEditProductAfterCompleted() = viewModelScope.launch {
        appConfigRepository.invertEditProductAfterCompleted()
    }

    private fun invertCompletedWithCheckbox() = viewModelScope.launch {
        appConfigRepository.invertCompletedWithCheckbox()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertSaveProductToAutocompletes() = viewModelScope.launch {
        appConfigRepository.invertSaveProductToAutocompletes()
    }

    private fun invertDisplayDefaultAutocomplete() = viewModelScope.launch {
        appConfigRepository.invertDisplayDefaultAutocompletes()
    }

    private fun invertDisplayOtherFields() = viewModelScope.launch {
        appConfigRepository.invertDisplayOtherFields()
    }

    private fun enterToSaveProducts() = viewModelScope.launch {
        appConfigRepository.invertEnterToSaveProduct()
    }

    private fun invertColoredCheckbox() = viewModelScope.launch {
        appConfigRepository.invertColoredCheckbox()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun sendEmailToDeveloper() = viewModelScope.launch(AppDispatchers.Main) {
        val event = SettingsScreenEvent.SendEmailToDeveloper(settingsState.getSettings().developerEmail)
        _screenEventFlow.emit(event)
    }

    private fun showBackScreen() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowNavigationDrawer)
    }

    private fun showAppGithub() = viewModelScope.launch(AppDispatchers.Main) {
        val link = settingsState.getSettings().appGithubLink
        val event = SettingsScreenEvent.ShowAppGithub(link)
        _screenEventFlow.emit(event)
    }

    private fun showPrivacyPolicy() = viewModelScope.launch(AppDispatchers.Main) {
        val link = settingsState.getSettings().privacyPolicyLink
        val event = SettingsScreenEvent.ShowPrivacyPolicy(link)
        _screenEventFlow.emit(event)
    }

    private fun showTermsAndConditions() = viewModelScope.launch(AppDispatchers.Main) {
        val link = settingsState.getSettings().termsAndConditionsLink
        val event = SettingsScreenEvent.ShowTermsAndConditions(link)
        _screenEventFlow.emit(event)
    }

    private fun hideFontSize() {
        settingsState.onSelectUid(false, SettingUid.FontSize)
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(SettingsScreenEvent.HideNavigationDrawer)
    }

    private fun hideDisplayCompletedPurchases() {
        settingsState.onSelectUid(false, SettingUid.DisplayCompletedPurchases)
    }

    private fun hideDisplayShoppingsProducts() {
        settingsState.onSelectUid(false, SettingUid.DisplayShoppingsProducts)
    }
}