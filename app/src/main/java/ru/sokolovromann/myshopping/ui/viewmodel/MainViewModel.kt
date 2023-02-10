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
import ru.sokolovromann.myshopping.data.repository.MainRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.MainState
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatchers: AppDispatchers,
    private val notificationManager: PurchasesNotificationManager,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<MainEvent> {

    val mainState = MainState()

    private val _screenEventFlow: MutableSharedFlow<MainScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MainScreenEvent> = _screenEventFlow

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnCreate -> onCreate(event)

            is MainEvent.AddDefaultPreferences -> addDefaultPreferences(event)

            is MainEvent.MigrateFromAppVersion14 -> migrateFromAppVersion14(event)
        }
    }

    private fun onCreate(event: MainEvent.OnCreate) = viewModelScope.launch {
        withContext(dispatchers.main) {
            mainState.showLoading()
        }

        repository.getMainPreferences().collect {
            applyMainPreferences(it, event)
        }
    }

    private suspend fun applyMainPreferences(
        mainPreferences: MainPreferences,
        event: MainEvent.OnCreate
    ) = withContext(dispatchers.main) {
        mainState.applyPreferences(mainPreferences)

        when (mainPreferences.appOpenedAction) {
            AppOpenedAction.NOTHING -> showPurchasesOrProducts(event.shoppingUid)

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
    ) = viewModelScope.launch {
        notificationManager.createNotificationChannel()

        val appVersion14 = repository.getAppVersion14().firstOrNull() ?: AppVersion14()

        val migrates = listOf(
            viewModelScope.async { migrateShoppings(appVersion14.shoppingLists) },
            viewModelScope.async { migrateAutocompletes(appVersion14.autocompletes) },
            viewModelScope.async {
                migrateSettings(
                    appVersion14.preferences,
                    toScreenSize(event.screenWidth)
                )
            }
        )
        migrates.awaitAll()

        showPurchasesOrProducts(null)
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
        repository.addShoppingListsDisplayTotal(preferences.displayTotal)
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

        val shoppingsMultiColumns = event.screenWidth >= 600
        repository.addShoppingListsMultiColumns(shoppingsMultiColumns)

        val productsMultiColumns = event.screenWidth >= 720
        repository.addProductsMultiColumns(productsMultiColumns)

        repository.addScreenSize(toScreenSize(event.screenWidth))
        repository.addAppOpenedAction(AppOpenedAction.NOTHING)

        notificationManager.createNotificationChannel()
    }

    private suspend fun showPurchasesOrProducts(
        shoppingUid: String?
    ) = viewModelScope.launch(dispatchers.main) {
        mainState.showContent()

        if (shoppingUid != null) {
            val event = MainScreenEvent.ShowProducts(shoppingUid)
            _screenEventFlow.emit(event)
        }
    }

    private fun toScreenSize(screenWidth: Int): ScreenSize {
        return if (screenWidth >= 720) {
            ScreenSize.TABLET
        } else {
            ScreenSize.SMARTPHONE
        }
    }
}