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
import ru.sokolovromann.myshopping.BuildConfig
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
            MainEvent.OnCreate -> onCreate()

            is MainEvent.OnStart -> onStart(event)

            MainEvent.OnStop -> onStop()

            is MainEvent.AddDefaultPreferences -> addDefaultPreferences(event)

            is MainEvent.MigrateFromCodeVersion14 -> migrateFromCodeVersion14(event)
        }
    }

    private fun onCreate() = viewModelScope.launch {
        withContext(dispatchers.main) {
            mainState.showLoading()
        }

        repository.getAppPreferences().collect {
            applyAppPreferences(it)
        }
    }

    private fun onStart(event: MainEvent.OnStart) = viewModelScope.launch {
        if (event.shoppingUid != null) {
            mainState.showProducts(event.shoppingUid)
        }
    }

    private fun onStop() = viewModelScope.launch {
        mainState.clearShoppingUid()
    }

    private suspend fun applyAppPreferences(appPreferences: AppPreferences) = withContext(dispatchers.main) {
        mainState.applyPreferences(appPreferences)

        when (appPreferences.appFirstTime) {
            AppFirstTime.NOTHING -> mainState.hideLoading()

            AppFirstTime.FIRST_TIME -> getDefaultPreferences()

            AppFirstTime.FIRST_TIME_FROM_APP_VERSION_14 -> getScreenSize()
        }
    }

    private fun getDefaultPreferences() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetDefaultPreferences)
    }

    private fun getScreenSize() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetScreenSize)
    }

    private fun migrateFromCodeVersion14(
        event: MainEvent.MigrateFromCodeVersion14
    ) = viewModelScope.launch {
        notificationManager.createNotificationChannel()

        val codeVersion14 = repository.getCodeVersion14().firstOrNull() ?: CodeVersion14()

        val migrates = listOf(
            viewModelScope.async { migrateShoppings(codeVersion14.shoppingLists) },
            viewModelScope.async { migrateAutocompletes(codeVersion14.autocompletes) },
            viewModelScope.async {
                migrateSettings(
                    codeVersion14.preferences,
                    toSmartphoneScreen(event.screenWidth)
                )
            }
        )
        migrates.awaitAll()

        mainState.hideLoading()
    }

    private suspend fun migrateShoppings(list: List<ShoppingList>) {
        list.forEach {
            repository.addShoppingList(it)
            if (it.reminder != null) {
                alarmManager.deleteCodeVersion14Reminder(it.id)
                alarmManager.createReminder(it.uid, it.reminder)
            }
        }
    }

    private suspend fun migrateAutocompletes(list: List<Autocomplete>) {
        list.forEach { repository.addAutocomplete(it) }
    }

    private suspend fun migrateSettings(
        preferences: CodeVersion14Preferences,
        smartphoneScreen: Boolean
    ) {
        val appPreferences = AppPreferences(
            appFirstTime = AppFirstTime.NOTHING,
            firstAppVersion = CodeVersion14.CODE_VERSION,
            fontSize = preferences.fontSize,
            smartphoneScreen = smartphoneScreen,
            currency = preferences.currency,
            taxRate = preferences.taxRate,
            shoppingsMultiColumns = preferences.multiColumns,
            productsMultiColumns = preferences.multiColumns,
            displayPurchasesTotal = preferences.displayTotal,
            editProductAfterCompleted = preferences.editProductAfterCompleted,
            saveProductToAutocompletes = preferences.saveProductToAutocompletes,
            displayMoney = preferences.displayMoney,
            completedWithCheckbox = false
        )
        repository.addPreferences(appPreferences)
    }

    private fun addDefaultPreferences(
        event: MainEvent.AddDefaultPreferences
    ) = viewModelScope.launch(dispatchers.io) {
        val appPreferences = AppPreferences(
            appFirstTime = AppFirstTime.NOTHING,
            firstAppVersion = BuildConfig.VERSION_CODE,
            smartphoneScreen = toSmartphoneScreen(event.screenWidth),
            currency = repository.getDefaultCurrency().firstOrNull() ?: Currency(),
            shoppingsMultiColumns = event.screenWidth >= 550,
            productsMultiColumns = event.screenWidth >= 720
        )
        repository.addPreferences(appPreferences)

        notificationManager.createNotificationChannel()
    }

    private fun toSmartphoneScreen(screenWidth: Int): Boolean {
        return screenWidth < 600
    }
}