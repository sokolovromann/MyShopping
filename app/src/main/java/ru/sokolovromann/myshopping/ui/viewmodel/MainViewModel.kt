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
import ru.sokolovromann.myshopping.data.model.AppBuildConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.AppOpenHelper
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.CodeVersion14
import ru.sokolovromann.myshopping.data.model.CodeVersion14Preferences
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.DeviceConfig
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.CodeVersion14Repository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.MainState
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val codeVersion14Repository: CodeVersion14Repository,
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
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

            is MainEvent.AddDefaultDeviceConfig -> addAppConfig(event)

            is MainEvent.MigrateFromCodeVersion14 -> migrateFromCodeVersion14(event)
        }
    }

    private fun onCreate() = viewModelScope.launch {
        withContext(dispatchers.main) {
            mainState.showLoading()
        }

        appConfigRepository.getAppConfig().collect {
            applyAppConfig(it)
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

    private suspend fun applyAppConfig(appConfig: AppConfig) = withContext(dispatchers.main) {
        mainState.applyAppConfig(appConfig)

        when (appConfig.appBuildConfig.getOpenHelper()) {
            AppOpenHelper.Create -> getDefaultDeviceConfig()

            AppOpenHelper.Open -> mainState.hideLoading()

            AppOpenHelper.Migrate -> migrate(appConfig)

            is AppOpenHelper.Error -> {}
        }
    }

    private fun getDefaultDeviceConfig() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetDefaultDeviceConfig)
    }

    private fun migrate(appConfig: AppConfig) {
        when (appConfig.appBuildConfig.userCodeVersion) {
            AppBuildConfig.CODE_VERSION_14 -> getScreenSize()
            else -> mainState.hideLoading()
        }
    }

    private fun getScreenSize() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(MainScreenEvent.GetScreenSize)
    }

    private fun migrateFromCodeVersion14(
        event: MainEvent.MigrateFromCodeVersion14
    ) = viewModelScope.launch {
        notificationManager.createNotificationChannel()

        val codeVersion14 = codeVersion14Repository.getCodeVersion14().firstOrNull() ?: CodeVersion14()

        val migrates = listOf(
            viewModelScope.async { migrateShoppings(codeVersion14.shoppingLists) },
            viewModelScope.async { migrateAutocompletes(codeVersion14.autocompletes) },
            viewModelScope.async {
                migrateSettings(
                    codeVersion14.preferences,
                    event
                )
            }
        )
        migrates.awaitAll()

        mainState.hideLoading()
    }

    private suspend fun migrateShoppings(list: List<ShoppingList>) {
        list.forEach {
            shoppingListsRepository.saveShoppingLists(list)

            val shopping = it.shopping
            if (shopping.reminder != null) {
                alarmManager.deleteCodeVersion14Reminder(shopping.id)
                alarmManager.createReminder(shopping.uid, shopping.reminder.millis)
            }
        }
    }

    private suspend fun migrateAutocompletes(list: List<Autocomplete>) {
        autocompletesRepository.saveAutocompletes(list)
    }

    private suspend fun migrateSettings(
        preferences: CodeVersion14Preferences,
        event: MainEvent.MigrateFromCodeVersion14
    ) {
        val deviceConfig = DeviceConfig(
            screenWidthDp = event.screenWidth,
            screenHeightDp = event.screenHeight
        )

        val appBuildConfig = AppBuildConfig(
            userCodeVersion = BuildConfig.VERSION_CODE
        )

        val userPreferences = UserPreferences(
            fontSize = preferences.fontSize,
            currency = preferences.currency,
            taxRate = preferences.taxRate,
            shoppingsMultiColumns = preferences.multiColumns,
            productsMultiColumns = preferences.multiColumns,
            displayTotal = preferences.displayTotal,
            editProductAfterCompleted = preferences.editProductAfterCompleted,
            saveProductToAutocompletes = preferences.saveProductToAutocompletes,
            displayMoney = preferences.displayMoney,
            completedWithCheckbox = false
        )

        val appConfig = AppConfig(
            deviceConfig = deviceConfig,
            appBuildConfig = appBuildConfig,
            userPreferences = userPreferences
        )

        appConfigRepository.saveAppConfig(appConfig)
    }

    private fun addAppConfig(
        event: MainEvent.AddDefaultDeviceConfig
    ) = viewModelScope.launch(dispatchers.io) {
        val deviceConfig = DeviceConfig(
            screenWidthDp = event.screenWidth,
            screenHeightDp = event.screenHeight
        )

        val appBuildConfig = AppBuildConfig(
            userCodeVersion = BuildConfig.VERSION_CODE
        )

        val userPreferences = UserPreferences(
            currency = appConfigRepository.getDefaultCurrency().firstOrNull() ?: Currency()
        )

        val appConfig = AppConfig(
            deviceConfig = deviceConfig,
            appBuildConfig = appBuildConfig,
            userPreferences = userPreferences
        )

        appConfigRepository.saveAppConfig(appConfig)

        notificationManager.createNotificationChannel()
    }
}