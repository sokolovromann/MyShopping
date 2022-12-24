package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.SettingsRepository
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.ScreenSize
import ru.sokolovromann.myshopping.data.repository.model.Settings
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers
) : ViewModel(), ViewModelEvent<SettingsEvent> {

    val settingsState: MapState<TextData, List<SettingsItem>> = MapState()

    val fontSizeState: ItemMenuState<FontSizeMenu> = ItemMenuState()

    val displayAutocompleteState: ItemMenuState<DisplayAutocompleteMenu> = ItemMenuState()

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _screenEventFlow: MutableSharedFlow<SettingsScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<SettingsScreenEvent> = _screenEventFlow

    init {
        showTopBar()
        getSettings()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SelectSettingsItem -> selectSettingsItem(event)

            is SettingsEvent.SelectNavigationItem -> selectNavigationItem(event)

            SettingsEvent.DisplayProductsAllAutocomplete -> displayProductsAllAutocomplete()

            SettingsEvent.DisplayProductsNameAutocomplete -> displayProductsNameAutocomplete()

            SettingsEvent.TinyFontSizeSelected -> tinyFontSizeSelected()

            SettingsEvent.SmallFontSizeSelected -> smallFontSizeSelected()

            SettingsEvent.MediumFontSizeSelected -> mediumFontSizeSelected()

            SettingsEvent.LargeFontSizeSelected -> largeFontSizeSelected()

            SettingsEvent.HugeFontSizeSelected -> hugeFontSizeSelected()

            SettingsEvent.ShowBackScreen -> showBackScreen()

            SettingsEvent.ShowNavigationDrawer -> showNavigationDrawer()

            SettingsEvent.HideFontSize -> hideFontSize()

            SettingsEvent.HideNavigationDrawer -> hideNavigationDrawer()

            SettingsEvent.HideProductsAutocomplete -> hideProductsAutocomplete()

            SettingsEvent.HideProductsDisplayAutocomplete -> hideProductsDisplayAutocomplete()
        }
    }

    private fun getSettings() = viewModelScope.launch(dispatchers.io) {
        showSettingsLoading()

        repository.getSettings().collect {
            showSettings(it)
        }
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

            SettingsUid.EditCompleted -> invertProductsEditCompleted()

            SettingsUid.AddProduct -> invertProductsAddLastProducts()

            SettingsUid.Email -> sendEmailToDeveloper()

            SettingsUid.Github -> showAppGithub()
        }
    }

    private fun selectFontSize() {
        fontSizeState.showMenu(SettingsUid.FontSize.name)
    }

    private fun selectProductsDisplayAutocomplete() {
        displayAutocompleteState.showMenu(SettingsUid.DisplayAutocomplete.name)
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

    private fun displayProductsAllAutocomplete() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductsAutocompleteAll()

        withContext(dispatchers.main) {
            hideProductsDisplayAutocomplete()
        }
    }

    private fun displayProductsNameAutocomplete() = viewModelScope.launch(dispatchers.io) {
        repository.displayProductAutocompleteName()

        withContext(dispatchers.main) {
            hideProductsDisplayAutocomplete()
        }
    }

    private fun tinyFontSizeSelected() = viewModelScope.launch(dispatchers.io) {
        repository.tinyFontSizeSelected()

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun smallFontSizeSelected() = viewModelScope.launch(dispatchers.io) {
        repository.smallFontSizeSelected()

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun mediumFontSizeSelected() = viewModelScope.launch(dispatchers.io) {
        repository.mediumFontSizeSelected()

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun largeFontSizeSelected() = viewModelScope.launch(dispatchers.io) {
        repository.largeFontSizeSelected()

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun hugeFontSizeSelected() = viewModelScope.launch(dispatchers.io) {
        repository.hugeFontSizeSelected()

        withContext(dispatchers.main) {
            hideFontSize()
        }
    }

    private fun invertNightTheme() = viewModelScope.launch(dispatchers.io) {
        repository.invertNightTheme()
    }

    private fun invertDisplayMoney() = viewModelScope.launch(dispatchers.io) {
        repository.invertDisplayMoney()
    }

    private fun invertDisplayCurrencyToLeft() = viewModelScope.launch(dispatchers.io) {
        repository.invertDisplayCurrencyToLeft()
    }

    private fun invertFirstLetterUppercase() = viewModelScope.launch(dispatchers.io) {
        repository.invertFirstLetterUppercase()
    }

    private fun invertShoppingListsMultiColumns() = viewModelScope.launch(dispatchers.io) {
        repository.invertShoppingListsMultiColumns()
    }

    private fun invertProductsMultiColumns() = viewModelScope.launch(dispatchers.io) {
        repository.invertProductsMultiColumns()
    }

    private fun invertProductsEditCompleted() = viewModelScope.launch(dispatchers.io) {
        repository.invertProductsEditCompleted()
    }

    private fun invertProductsAddLastProducts() = viewModelScope.launch(dispatchers.io) {
        repository.invertProductsAddLastProduct()
    }

    private fun sendEmailToDeveloper() = viewModelScope.launch(dispatchers.io) {
        val email = repository.getSettings().firstOrNull()
            ?.settingsValues?.developerEmail ?: return@launch
        val subject = "MyShopping"

        withContext(dispatchers.main) {
            _screenEventFlow.emit(SettingsScreenEvent.SendEmailToDeveloper(email, subject))
        }
    }

    private suspend fun showSettingsLoading() = withContext(dispatchers.main) {
        settingsState.showLoading()
    }

    private suspend fun showSettings(settings: Settings) = withContext(dispatchers.main) {
        val preferences = settings.preferences

        val items = mapOf(
            mapping.toSettingsHeader(R.string.settings_header_generalSettings, preferences)
                    to mapping.toGeneralSettingsItems(settings),
            mapping.toSettingsHeader(R.string.settings_header_money, preferences)
                    to mapping.toMoneySettingsItems(settings),
            mapping.toSettingsHeader(R.string.settings_header_purchases, preferences)
                    to mapping.toPurchasesSettingsItems(settings),
            mapping.toSettingsHeader(R.string.settings_header_aboutApp, preferences)
                    to mapping.toAboutSettingsItems(settings)
        )

        settingsState.showMap(
            items = items,
            multiColumns = preferences.screenSize == ScreenSize.TABLET
        )

        fontSizeState.setMenu(mapping.toFontSizeMenu(preferences.fontSize))
        displayAutocompleteState.setMenu(mapping.toDisplayAutocompleteMenu(
            settings.settingsValues.productsDisplayAutocomplete,
            preferences.fontSize
        ))
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowBackScreen)
    }

    private fun showNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.ShowNavigationDrawer)
    }

    private fun showAppGithub() = viewModelScope.launch(dispatchers.io) {
        val githubLink = repository.getSettings().firstOrNull()
            ?.settingsValues?.appGithubLink ?: return@launch

        withContext(dispatchers.main) {
            _screenEventFlow.emit(SettingsScreenEvent.ShowAppGithub(githubLink))
        }
    }

    private fun showTopBar() {
        val data = TopBarData(
            title = mapping.toOnTopAppBarHeader(
                text = mapping.toResourcesUiText(R.string.settings_header_settings),
                fontSize = FontSize.MEDIUM
            ),
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.Menu)
            )
        )
        _topBarState.value = data
    }

    private fun hideFontSize() {
        fontSizeState.hideMenu()
    }

    private fun hideNavigationDrawer() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(SettingsScreenEvent.HideNavigationDrawer)
    }

    private fun hideProductsAutocomplete() = viewModelScope.launch(dispatchers.io) {
        repository.hideProductsAutocomplete()

        withContext(dispatchers.main) {
            hideProductsDisplayAutocomplete()
        }
    }

    private fun hideProductsDisplayAutocomplete() {
        displayAutocompleteState.hideMenu()
    }
}