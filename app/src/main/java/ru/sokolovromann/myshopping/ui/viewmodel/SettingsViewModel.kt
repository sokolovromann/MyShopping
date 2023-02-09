package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.SettingsRepository
import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
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

            SettingsEvent.SelectDisplayCompleted -> selectDisplayCompleted()

            is SettingsEvent.FontSizeSelected -> fontSizeSelected(event)

            is SettingsEvent.DisplayAutocompleteSelected -> displayAutocompleteSelected(event)

            is SettingsEvent.DisplayCompletedSelected -> displayCompletedSelected(event)

            SettingsEvent.ShowBackScreen -> showBackScreen()

            SettingsEvent.ShowNavigationDrawer -> showNavigationDrawer()

            SettingsEvent.HideFontSize -> hideFontSize()

            SettingsEvent.HideNavigationDrawer -> hideNavigationDrawer()

            SettingsEvent.HideProductsDisplayAutocomplete -> hideProductsDisplayAutocomplete()

            SettingsEvent.HideDisplayCompleted -> hideDisplayCompleted()
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

            SettingsUid.FirstLetterUppercase -> invertFirstLetterUppercase()

            SettingsUid.DisplayMoney -> invertDisplayMoney()

            SettingsUid.Currency -> editCurrencySymbol()

            SettingsUid.DisplayCurrencyToLeft -> invertDisplayCurrencyToLeft()

            SettingsUid.TaxRate -> editTaxRate()

            SettingsUid.ShoppingsMultiColumns -> invertShoppingListsMultiColumns()

            SettingsUid.ProductsMultiColumns -> invertProductsMultiColumns()

            SettingsUid.DisplayAutocomplete -> selectProductsDisplayAutocomplete()

            SettingsUid.DisplayCompleted -> selectDisplayCompleted()

            SettingsUid.EditCompleted -> invertProductsEditCompleted()

            SettingsUid.AddProduct -> invertProductsAddLastProducts()

            SettingsUid.Email -> sendEmailToDeveloper()

            SettingsUid.Github -> showAppGithub()
        }
    }

    private fun selectFontSize() {
        settingsState.showFontSize()
    }

    private fun selectProductsDisplayAutocomplete() {
        settingsState.showDisplayAutocomplete()
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

    private fun selectDisplayCompleted() {
        settingsState.showDisplayCompleted()
    }

    private fun fontSizeSelected(
        event: SettingsEvent.FontSizeSelected
    ) = viewModelScope.launch {
        when (event.fontSize) {
            FontSize.TINY -> repository.tinyFontSizeSelected()
            FontSize.SMALL -> repository.smallFontSizeSelected()
            FontSize.MEDIUM -> repository.mediumFontSizeSelected()
            FontSize.LARGE -> repository.largeFontSizeSelected()
            FontSize.HUGE -> repository.hugeFontSizeSelected()
        }

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun displayAutocompleteSelected(
        event: SettingsEvent.DisplayAutocompleteSelected
    ) = viewModelScope.launch {
        when (event.displayAutocomplete) {
            DisplayAutocomplete.ALL -> repository.displayProductsAutocompleteAll()
            DisplayAutocomplete.NAME -> repository.displayProductAutocompleteName()
            DisplayAutocomplete.HIDE -> repository.hideProductsAutocomplete()
        }

        withContext(dispatchers.main) {
            hideProductsDisplayAutocomplete()
        }
    }

    private fun displayCompletedSelected(
        event: SettingsEvent.DisplayCompletedSelected
    ) = viewModelScope.launch {
        when (event.displayCompleted) {
            DisplayCompleted.FIRST -> repository.displayCompletedFirst()
            DisplayCompleted.LAST -> repository.displayCompletedLast()
            DisplayCompleted.HIDE -> repository.hideCompleted()
        }

        withContext(dispatchers.main) {
            hideDisplayCompleted()
        }
    }

    private fun invertNightTheme() = viewModelScope.launch {
        repository.invertNightTheme()
    }

    private fun invertDisplayMoney() = viewModelScope.launch {
        repository.invertDisplayMoney()
    }

    private fun invertDisplayCurrencyToLeft() = viewModelScope.launch {
        repository.invertDisplayCurrencyToLeft()
    }

    private fun invertFirstLetterUppercase() = viewModelScope.launch {
        repository.invertFirstLetterUppercase()
    }

    private fun invertShoppingListsMultiColumns() = viewModelScope.launch {
        repository.invertShoppingListsMultiColumns()
    }

    private fun invertProductsMultiColumns() = viewModelScope.launch {
        repository.invertProductsMultiColumns()
    }

    private fun invertProductsEditCompleted() = viewModelScope.launch {
        repository.invertProductsEditCompleted()
    }

    private fun invertProductsAddLastProducts() = viewModelScope.launch {
        repository.invertProductsAddLastProduct()
    }

    private fun sendEmailToDeveloper() = viewModelScope.launch {
        val email = repository.getSettings().firstOrNull()
            ?.settingsValues?.developerEmail ?: return@launch
        val subject = "MyShopping"

        withContext(dispatchers.main) {
            _screenEventFlow.emit(SettingsScreenEvent.SendEmailToDeveloper(email, subject))
        }
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowNavigationDrawer)
    }

    private fun showAppGithub() = viewModelScope.launch {
        val githubLink = repository.getSettings().firstOrNull()
            ?.settingsValues?.appGithubLink ?: return@launch

        withContext(dispatchers.main) {
            _screenEventFlow.emit(SettingsScreenEvent.ShowAppGithub(githubLink))
        }
    }

    private fun hideFontSize() {
        settingsState.hideFontSize()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.HideNavigationDrawer)
    }

    private fun hideProductsDisplayAutocomplete() {
        settingsState.hideDisplayAutocomplete()
    }

    private fun hideDisplayCompleted() {
        settingsState.hideDisplayCompleted()
    }
}