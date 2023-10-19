package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val dispatchers: AppDispatchers
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
        withContext(dispatchers.main) {
            settingsState.showLoading()
        }

        appConfigRepository.getSettingsWithConfig().collect {
            settingsLoaded(it)
        }
    }

    private suspend fun settingsLoaded(
        settingsWithConfig: SettingsWithConfig
    ) = withContext(dispatchers.main) {
        val settings = AppConfigMapper.toRepositorySettings(settingsWithConfig)
        settingsState.showSetting(settings)
    }

    private fun editCurrencySymbol() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.EditCurrency)
    }

    private fun editTaxRate() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.EditTaxRate)
    }

    private fun selectSettingsItem(event: SettingsEvent.SelectSettingsItem) {
        when (event.uid) {
            SettingsUid.NoUId -> return

            SettingsUid.NightTheme -> invertNightTheme()

            SettingsUid.FontSize -> selectFontSize()

            SettingsUid.Backup -> backup()

            SettingsUid.DisplayMoney -> invertDisplayMoney()

            SettingsUid.Currency -> editCurrencySymbol()

            SettingsUid.DisplayCurrencyToLeft -> invertDisplayCurrencyToLeft()

            SettingsUid.DisplayMoneyZeros -> invertDisplayMoneyZeros()

            SettingsUid.TaxRate -> editTaxRate()

            SettingsUid.DisplayDefaultAutocomplete -> invertDisplayDefaultAutocomplete()

            SettingsUid.DisplayCompletedPurchases -> selectDisplayCompletedPurchases()

            SettingsUid.DisplayOtherFields -> invertDisplayOtherFields()

            SettingsUid.EditProductAfterCompleted -> invertEditProductAfterCompleted()

            SettingsUid.CompletedWithCheckbox -> invertCompletedWithCheckbox()

            SettingsUid.DisplayShoppingsProducts -> selectShoppingsProducts()

            SettingsUid.EnterToSaveProducts -> enterToSaveProducts()

            SettingsUid.ColoredCheckbox -> invertColoredCheckbox()

            SettingsUid.SaveProductToAutocompletes -> invertSaveProductToAutocompletes()

            SettingsUid.MigrateFromCodeVersion14 -> {}

            SettingsUid.Email -> sendEmailToDeveloper()

            SettingsUid.Github -> showAppGithub()

            SettingsUid.PrivacyPolicy -> showPrivacyPolicy()

            SettingsUid.TermsAndConditions -> showTermsAndConditions()
        }
    }

    private fun selectFontSize() {
        settingsState.showFontSize()
    }

    private fun backup() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackup)
    }

    private fun selectShoppingsProducts() {
        settingsState.showShoppingsProducts()
    }

    private fun selectNavigationItem(
        event: SettingsEvent.SelectNavigationItem
    ) = viewModelScope.launch(dispatchers.main) {
        when (event.route) {
            UiRoute.Purchases -> _screenEventFlow.emit(SettingsScreenEvent.ShowPurchases)
            UiRoute.Archive -> _screenEventFlow.emit(SettingsScreenEvent.ShowArchive)
            UiRoute.Trash -> _screenEventFlow.emit(SettingsScreenEvent.ShowTrash)
            UiRoute.Autocompletes -> _screenEventFlow.emit(SettingsScreenEvent.ShowAutocompletes)
            else -> return@launch
        }
    }

    private fun selectDisplayCompletedPurchases() {
        settingsState.showDisplayCompletedPurchases()
    }

    private fun fontSizeSelected(
        event: SettingsEvent.FontSizeSelected
    ) = viewModelScope.launch {
        appConfigRepository.saveFontSize(event.fontSize)

        withContext(dispatchers.main) {
            hideFontSize()
            _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
        }
    }

    private fun displayCompletedPurchasesSelected(
        event: SettingsEvent.DisplayCompletedPurchasesSelected
    ) = viewModelScope.launch {
        appConfigRepository.displayCompleted(event.displayCompleted)

        withContext(dispatchers.main) {
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

    private fun sendEmailToDeveloper() = viewModelScope.launch(dispatchers.main) {
        val event = SettingsScreenEvent.SendEmailToDeveloper(settingsState.getDeveloperEmail())
        _screenEventFlow.emit(event)
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowNavigationDrawer)
    }

    private fun showAppGithub() = viewModelScope.launch(dispatchers.main) {
        val link = settingsState.getAppGithubLinkResult()
            .getOrElse { return@launch }
        val event = SettingsScreenEvent.ShowAppGithub(link)
        _screenEventFlow.emit(event)
    }

    private fun showPrivacyPolicy() = viewModelScope.launch(dispatchers.main) {
        val link = settingsState.getPrivacyPolicyLinkResult()
            .getOrElse { return@launch }
        val event = SettingsScreenEvent.ShowPrivacyPolicy(link)
        _screenEventFlow.emit(event)
    }

    private fun showTermsAndConditions() = viewModelScope.launch(dispatchers.main) {
        val link = settingsState.getTermsAndConditionsLinkResult()
            .getOrElse { return@launch }
        val event = SettingsScreenEvent.ShowTermsAndConditions(link)
        _screenEventFlow.emit(event)
    }

    private fun hideFontSize() {
        settingsState.hideFontSize()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.HideNavigationDrawer)
    }

    private fun hideDisplayCompletedPurchases() {
        settingsState.hideDisplayCompletedPurchases()
    }

    private fun hideDisplayShoppingsProducts() {
        settingsState.hideDisplayShoppingsProducts()
    }
}