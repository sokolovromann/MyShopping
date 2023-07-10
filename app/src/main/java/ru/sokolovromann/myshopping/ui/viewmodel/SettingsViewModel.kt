package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.SettingsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val dispatchers: AppDispatchers,
    private val alarmManager: PurchasesAlarmManager
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

        repository.getSettings().collect {
            settingsLoaded(it)
        }
    }

    private suspend fun settingsLoaded(settings: Settings) = withContext(dispatchers.main) {
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

            SettingsUid.DisplayMoney -> invertDisplayMoney()

            SettingsUid.Currency -> editCurrencySymbol()

            SettingsUid.DisplayCurrencyToLeft -> invertDisplayCurrencyToLeft()

            SettingsUid.TaxRate -> editTaxRate()

            SettingsUid.DisplayDefaultAutocomplete -> invertDisplayDefaultAutocomplete()

            SettingsUid.DisplayCompletedPurchases -> selectDisplayCompletedPurchases()

            SettingsUid.EditProductAfterCompleted -> invertEditProductAfterCompleted()

            SettingsUid.CompletedWithCheckbox -> invertCompletedWithCheckbox()

            SettingsUid.DisplayShoppingsProducts -> selectShoppingsProducts()

            SettingsUid.EnterToSaveProducts -> enterToSaveProducts()

            SettingsUid.HighlightCheckbox -> highlightCheckbox()

            SettingsUid.SaveProductToAutocompletes -> invertSaveProductToAutocompletes()

            SettingsUid.MigrateFromAppVersion14 -> migrateFromAppVersion14()

            SettingsUid.Email -> sendEmailToDeveloper()

            SettingsUid.Github -> showAppGithub()

            SettingsUid.PrivacyPolicy -> showPrivacyPolicy()

            SettingsUid.TermsAndConditions -> showTermsAndConditions()
        }
    }

    private fun selectFontSize() {
        settingsState.showFontSize()
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
        when (event.fontSize) {
            FontSize.SMALL -> repository.smallFontSizeSelected()
            FontSize.MEDIUM -> repository.mediumFontSizeSelected()
            FontSize.LARGE -> repository.largeFontSizeSelected()
            FontSize.HUGE -> repository.hugeFontSizeSelected()
            FontSize.HUGE_2 -> repository.huge2FontSizeSelected()
            FontSize.HUGE_3 -> repository.huge3FontSizeSelected()
        }

        withContext(dispatchers.main) {
            hideFontSize()
            _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
        }
    }

    private fun displayCompletedPurchasesSelected(
        event: SettingsEvent.DisplayCompletedPurchasesSelected
    ) = viewModelScope.launch {
        when (event.displayCompleted) {
            DisplayCompleted.FIRST -> repository.displayCompletedPurchasesFirst()
            DisplayCompleted.LAST -> repository.displayCompletedPurchasesLast()
            DisplayCompleted.HIDE -> repository.hideCompletedPurchases()
        }

        withContext(dispatchers.main) {
            hideDisplayCompletedPurchases()
            _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
        }
    }

    private fun displayShoppingsProductsSelected(
        event: SettingsEvent.DisplayShoppingsProductsSelected
    ) = viewModelScope.launch {
        when (event.displayProducts) {
            DisplayProducts.COLUMNS -> repository.displayShoppingsProductsColumns()
            DisplayProducts.ROW -> repository.displayShoppingsProductsRow()
            DisplayProducts.HIDE -> repository.hideShoppingsProducts()
        }
    }

    private fun invertNightTheme() = viewModelScope.launch {
        repository.invertNightTheme()
    }

    private fun invertDisplayMoney() = viewModelScope.launch {
        repository.invertDisplayMoney()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertDisplayCurrencyToLeft() = viewModelScope.launch {
        repository.invertDisplayCurrencyToLeft()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertEditProductAfterCompleted() = viewModelScope.launch {
        repository.invertEditProductAfterCompleted()
    }

    private fun invertCompletedWithCheckbox() = viewModelScope.launch {
        repository.invertCompletedWithCheckbox()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun invertSaveProductToAutocompletes() = viewModelScope.launch {
        repository.invertSaveProductToAutocompletes()
    }

    private fun invertDisplayDefaultAutocomplete() = viewModelScope.launch {
        repository.invertDisplayDefaultAutocompletes()
    }

    private fun enterToSaveProducts() = viewModelScope.launch {
        repository.invertEnterToSaveProduct()
    }

    private fun highlightCheckbox() = viewModelScope.launch {
        repository.invertHighlightCheckbox()
        _screenEventFlow.emit(SettingsScreenEvent.UpdateProductsWidgets)
    }

    private fun migrateFromAppVersion14() = viewModelScope.launch {
        withContext(dispatchers.main) {
            settingsState.showLoading()
        }

        repository.getReminderUids().firstOrNull()?.let { ids ->
            ids.forEach { alarmManager.deleteReminder(it) }
        }

        repository.deleteAppData()
            .onSuccess {
                val appVersion14 = repository.getAppVersion14().firstOrNull() ?: AppVersion14()

                val migrates = listOf(
                    viewModelScope.async { migrateShoppings(appVersion14.shoppingLists) },
                    viewModelScope.async { migrateAutocompletes(appVersion14.autocompletes) }
                )
                migrates.awaitAll()

                withContext(dispatchers.main) {
                    _screenEventFlow.emit(SettingsScreenEvent.ShowPurchases)
                }
            }
            .onFailure {
                withContext(dispatchers.main) {
                    _screenEventFlow.emit(SettingsScreenEvent.ShowPurchases)
                }
            }
    }

    private suspend fun migrateShoppings(list: List<ShoppingList>) {
        list.forEach {
            repository.addShoppingList(it)
            if (it.reminder != null) {
                alarmManager.deleteAppVersion14Reminder(it.id)
                alarmManager.createReminder(it.uid, it.reminder)
            }
        }
    }

    private suspend fun migrateAutocompletes(list: List<Autocomplete>) {
        list.forEach { repository.addAutocomplete(it) }
    }

    private fun sendEmailToDeveloper() = viewModelScope.launch {
        repository.getSettings().firstOrNull()?.let {
            withContext(dispatchers.main) {
                val event = SettingsScreenEvent.SendEmailToDeveloper(it.developerEmail)
                _screenEventFlow.emit(event)
            }
        }
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