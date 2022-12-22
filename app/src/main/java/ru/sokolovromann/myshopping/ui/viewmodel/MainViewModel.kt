package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import ru.sokolovromann.myshopping.data.repository.MainRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val notificationManager: PurchasesNotificationManager,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<MainEvent> {

    private val _nightThemeState: MutableState<Boolean> = mutableStateOf(false)
    val nightThemeState: State<Boolean> = _nightThemeState

    private val _loadingState: MutableState<Boolean> = mutableStateOf(true)
    val loadingState: State<Boolean> = _loadingState

    private val _screenEventFlow: MutableSharedFlow<MainScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MainScreenEvent> = _screenEventFlow

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnCreate -> onCreate(event)

            is MainEvent.AddDefaultPreferences -> addDefaultPreferences(event)

            is MainEvent.MigrateFromAppVersion14 -> migrateFromAppVersion14(event)
        }
    }

    private fun onCreate(event: MainEvent.OnCreate) = viewModelScope.launch(dispatchers.io) {
        showLoading()

        repository.getMainPreferences().collect {
            applyNightTheme(it.nightTheme)
            onOpened(it.appOpenedAction, event)
        }
    }

    private suspend fun onOpened(
        firstOpenedAction: AppOpenedAction,
        event: MainEvent.OnCreate
    ) = withContext(dispatchers.main) {
        when (firstOpenedAction) {
            AppOpenedAction.NOTHING -> if (event.shoppingUid == null) {
                showPurchases()
            } else {
                showProducts(event.shoppingUid)
            }

            AppOpenedAction.ADD_DEFAULT_DATA -> getDefaultPreferences()

            AppOpenedAction.MIGRATE_FROM_APP_VERSION_14 -> getScreenSize()
        }
    }

    private fun getDefaultPreferences() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetDefaultPreferences)
    }

    private fun getScreenSize() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetScreenSize)
    }

    private fun migrateFromAppVersion14(
        event: MainEvent.MigrateFromAppVersion14
    ) = viewModelScope.launch(dispatchers.io) {
        notificationManager.createNotificationChannel()

        val appVersion14 = repository.getAppVersion14().firstOrNull() ?: AppVersion14()

        val migrates = listOf(
            viewModelScope.async { migrateShoppings(appVersion14.shoppingLists) },
            viewModelScope.async { migrateAutocompletes(appVersion14.autocompletes) },
            viewModelScope.async {
                migrateSettings(
                    appVersion14.preferences,
                    mapping.toScreenSize(event.screenWidth)
                )
            }
        )
        migrates.awaitAll()

        showPurchases()
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

    private suspend fun migrateSettings(
        preferences: AppVersion14Preferences,
        screenSize: ScreenSize
    ) {
        repository.addAppOpenedAction(AppOpenedAction.NOTHING)
        repository.addCurrency(preferences.currency)
        repository.addTaxRate(preferences.taxRate)
        repository.addFontSize(preferences.fontSize)
        repository.addFirstLetterUppercase(preferences.firstLetterUppercase)
        repository.addShoppingListsMultiColumns(preferences.multiColumns)
        repository.addProductsMultiColumns(preferences.multiColumns)
        repository.addShoppingListsSort(preferences.sort)
        repository.addShoppingListsDisplayTotal(preferences.displayTotal)
        repository.addProductsSort(preferences.sort)
        repository.addProductsDisplayTotal(preferences.displayTotal)
        repository.addProductsEditCompleted(preferences.editCompleted)
        repository.addProductsAddLastProduct(preferences.addLastProduct)
        repository.addDisplayMoney(preferences.displayMoney)
        repository.addScreenSize(screenSize)
    }

    private fun addDefaultPreferences(
        event: MainEvent.AddDefaultPreferences
    ) = viewModelScope.launch(dispatchers.io) {
        val defaultCurrency = repository.getDefaultCurrency().firstOrNull() ?: Currency()
        repository.addCurrency(defaultCurrency)

        repository.addShoppingListsMultiColumns(mapping.toShoppingsMultiColumns(event.screenWidth))
        repository.addProductsMultiColumns(mapping.toProductsMultiColumns(event.screenWidth))
        repository.addScreenSize(mapping.toScreenSize(event.screenWidth))
        repository.addAppOpenedAction(AppOpenedAction.NOTHING)

        notificationManager.createNotificationChannel()
    }

    private suspend fun applyNightTheme(nightTheme: Boolean) = withContext(dispatchers.main) {
        _nightThemeState.value = nightTheme
    }

    private suspend fun showLoading() = withContext(dispatchers.main) {
        _loadingState.value = true
    }

    private suspend fun showPurchases() = withContext(dispatchers.main) {
        hideLoading()
    }

    private fun showProducts(shoppingUid: String) = viewModelScope.launch(dispatchers.main) {
        hideLoading()

        val event = MainScreenEvent.ShowProducts(shoppingUid)
        _screenEventFlow.emit(event)
    }

    private fun hideLoading() {
        _loadingState.value = false
    }
}