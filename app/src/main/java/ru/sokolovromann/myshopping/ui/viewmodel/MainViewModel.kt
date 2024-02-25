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
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppAction
import ru.sokolovromann.myshopping.app.AppDispatchers
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
import ru.sokolovromann.myshopping.ui.model.MainState
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val codeVersion14Repository: CodeVersion14Repository,
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
    private val notificationManager: PurchasesNotificationManager,
    private val alarmManager: PurchasesAlarmManager
) : ViewModel(), ViewModelEvent<MainEvent> {

    val mainState = MainState()

    private val _screenEventFlow: MutableSharedFlow<MainScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MainScreenEvent> = _screenEventFlow

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnCreate -> onCreate(event)

            is MainEvent.OnSaveIntent -> onSaveIntent(event)
        }
    }

    private fun onCreate(event: MainEvent.OnCreate) = viewModelScope.launch(AppDispatchers.Main) {
        mainState.onWaiting(displaySplashScreen = true)

        appConfigRepository.getAppConfig().collect {
            mainState.populate(it)

            when (it.appBuildConfig.getOpenHelper()) {
                AppOpenHelper.Open -> {
                    mainState.onWaiting(displaySplashScreen = false)
                }

                AppOpenHelper.Create -> {
                    onAddDefaultAppConfig(event)
                }

                AppOpenHelper.Migrate -> {
                    when (it.appBuildConfig.userCodeVersion) {
                        AppBuildConfig.CODE_VERSION_14 -> onMigrateFromCodeVersion14(event)
                        else -> appConfigRepository.saveUserCodeVersion(BuildConfig.VERSION_CODE)
                    }
                }

                is AppOpenHelper.Error -> {
                    _screenEventFlow.emit(MainScreenEvent.OnFinishApp)
                }
            }
        }
    }

    private fun onSaveIntent(event: MainEvent.OnSaveIntent) {
        if (event.action == AppAction.SHORTCUTS_ADD_SHOPPING_LIST) {
            onAddShoppingList()
        } else {
            if (event.action?.contains(AppAction.WIDGETS_OPEN_PRODUCTS_PREFIX) == true) {
                mainState.saveShoppingUid(event.uid)
            } else {
                mainState.saveShoppingUid(null)
            }
        }
    }

    private fun onAddShoppingList() = viewModelScope.launch(AppDispatchers.Main) {
        shoppingListsRepository.addShopping()
            .onSuccess { mainState.saveShoppingUid(it) }
    }

    private fun onAddDefaultAppConfig(
        event: MainEvent.OnCreate
    ) = viewModelScope.launch(AppDispatchers.Main) {
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

    private fun onMigrateFromCodeVersion14(
        event: MainEvent.OnCreate
    ) = viewModelScope.launch(AppDispatchers.Main) {
        notificationManager.createNotificationChannel()

        val codeVersion14 = codeVersion14Repository.getCodeVersion14().firstOrNull() ?: CodeVersion14()
        listOf(
            viewModelScope.async { migrateShoppings(codeVersion14.shoppingLists) },
            viewModelScope.async { migrateAutocompletes(codeVersion14.autocompletes) },
            viewModelScope.async { migrateSettings(codeVersion14.preferences, event) }
        ).awaitAll()
    }

    private suspend fun migrateShoppings(list: List<ShoppingList>) {
        shoppingListsRepository.saveShoppingLists(list)

        list.forEach {
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
        event: MainEvent.OnCreate
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
}