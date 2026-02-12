package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppAction
import ru.sokolovromann.myshopping.data.model.AppBuildConfig
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.AppOpenHelper
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.CodeVersion14
import ru.sokolovromann.myshopping.data.model.CodeVersion14Preferences
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.DeviceConfig
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.CodeVersion14Repository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data39.LocalEnvironment
import ru.sokolovromann.myshopping.data39.LocalResources
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDirectory
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsPreInstalled
import ru.sokolovromann.myshopping.manager.MigrationManager
import ru.sokolovromann.myshopping.manager.SuggestionsManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.compose.event.MainScreenEvent
import ru.sokolovromann.myshopping.ui.model.MainState
import ru.sokolovromann.myshopping.ui.shortcut.AppShortcutManager
import ru.sokolovromann.myshopping.ui.viewmodel.event.MainEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.async
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTimeAlias
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val codeVersion14Repository: CodeVersion14Repository,
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
    private val notificationManager: PurchasesNotificationManager,
    private val alarmManager: PurchasesAlarmManager,
    private val appShortcutManager: AppShortcutManager,
    private val migrationManager: MigrationManager,
    private val suggestionsManager: SuggestionsManager,
    private val localResources: LocalResources
) : ViewModel(), ViewModelEvent<MainEvent> {

    val mainState = MainState()

    private val _screenEventFlow: MutableSharedFlow<MainScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<MainScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnCreate -> onCreate(event)

            is MainEvent.OnSaveIntent -> onSaveIntent(event)
        }
    }

    private fun onCreate(event: MainEvent.OnCreate) = viewModelScope.launch(dispatcher) {
        mainState.onWaiting(displaySplashScreen = true)

        val shortcutsLimit = 2
        shoppingListsRepository.getShortcuts(shortcutsLimit).collect {
            mainState.populate(it.getUserPreferences())

            when (it.getAppBuildConfig().getOpenHelper()) {
                AppOpenHelper.Open -> {
                    onOpenApp(it.getUserPreferences()).await()
                    mainState.onWaiting(displaySplashScreen = false)
                }

                AppOpenHelper.Create -> {
                    onAddDefaultAppConfig(event)
                }

                AppOpenHelper.Migrate -> {
                    when (it.getAppBuildConfig().userCodeVersion) {
                        AppBuildConfig.CODE_VERSION_14 -> onMigrateFromCodeVersion14(event)
                        AppBuildConfig.CODE_VERSION_39 -> onMigrateFromCodeVersion39()
                        else -> appConfigRepository.saveUserCodeVersion(BuildConfig.VERSION_CODE)
                    }
                }

                is AppOpenHelper.Error -> {
                    _screenEventFlow.emit(MainScreenEvent.OnFinishApp)
                }
            }

            appShortcutManager.removeAllShoppingListsShortcuts()
            val shoppingLists = it.getSortedShoppingLists()
            if (shoppingLists.isNotEmpty()) {
                appShortcutManager.updateShoppingListsShortcuts(shoppingLists)
            }
        }
    }

    private fun onOpenApp(userPreferences: UserPreferences) = viewModelScope.async(dispatcher) {
        if (userPreferences.automaticallyEmptyTrash) {
            val millis = DateTime.getCurrentDateTime().millis - 864000000L // 10 days
            val dateTime = DateTime(millis)
            shoppingListsRepository.deleteShoppingListsBeforeDateTime(dateTime)
        }
    }

    private fun onSaveIntent(event: MainEvent.OnSaveIntent) {
        if (event.action == AppAction.SHORTCUTS) {
            if (event.uid == null) {
                onAddShoppingList()
            } else {
                mainState.saveShoppingUid(event.uid)
            }
        } else {
            if (event.action?.contains(AppAction.WIDGETS_OPEN_PRODUCTS_PREFIX) == true) {
                mainState.saveShoppingUid(event.uid)
            } else {
                mainState.saveShoppingUid(null)
            }
        }
    }

    private fun onAddShoppingList() = viewModelScope.launch(dispatcher) {
        shoppingListsRepository.addShopping().onSuccess { uid ->
            mainState.saveShoppingUid(
                uid = uid,
                after = appConfigRepository.getAppConfig().first().userPreferences.afterAddShopping
            )
        }
    }

    private fun onAddDefaultAppConfig(
        event: MainEvent.OnCreate
    ) = viewModelScope.launch(dispatcher) {
        val suggestions = localResources
            .getStrings(LocalEnvironment.DEFAULT_AUTOCOMPLETES_RES_ID)
            .map {
                val currentDateTime = DateTimeAlias.getCurrent()
                Suggestion(
                    uid = UID.createRandom(),
                    directory = SuggestionDirectory.NoDirectory,
                    created = currentDateTime,
                    lastModified = currentDateTime,
                    name = it,
                    used = 0
                )
            }
        suggestionsManager.apply {
            addSuggestions(suggestions)
            updateConfig(SuggestionsPreInstalled.DoNotAdd)
        }

        val deviceConfig = DeviceConfig(
            screenWidthDp = event.screenWidth,
            screenHeightDp = event.screenHeight
        )

        val appBuildConfig = AppBuildConfig(
            userCodeVersion = BuildConfig.VERSION_CODE
        )

        val userPreferences = UserPreferences(
            currency = appConfigRepository.getDefaultCurrency().firstOrNull() ?: Currency(),
            automaticallyEmptyTrash = true
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
    ) = viewModelScope.launch(dispatcher) {
        notificationManager.createNotificationChannel()

        val codeVersion14 = codeVersion14Repository.getCodeVersion14().firstOrNull() ?: CodeVersion14()
        listOf(
            viewModelScope.async(dispatcher) { migrateShoppings(codeVersion14.shoppingLists) },
            viewModelScope.async(dispatcher) { migrateAutocompletes(codeVersion14.autocompletes) },
            viewModelScope.async(dispatcher) { migrateSettings(codeVersion14.preferences, event) }
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
            appFontSize = preferences.fontSize,
            currency = preferences.currency,
            taxRate = preferences.taxRate,
            shoppingsMultiColumns = preferences.multiColumns,
            productsMultiColumns = preferences.multiColumns,
            displayTotal = preferences.displayTotal,
            editProductAfterCompleted = preferences.editProductAfterCompleted,
            saveProductToAutocompletes = preferences.saveProductToAutocompletes,
            displayMoney = preferences.displayMoney,
            completedWithCheckbox = false,
            automaticallyEmptyTrash = true
        )

        val appConfig = AppConfig(
            deviceConfig = deviceConfig,
            appBuildConfig = appBuildConfig,
            userPreferences = userPreferences
        )

        appConfigRepository.saveAppConfig(appConfig)
    }

    private fun onMigrateFromCodeVersion39() = viewModelScope.launch(dispatcher) {
        migrationManager.migrateApi15Autocompletes()
        migrationManager.migrateApi15AutocompletesConfig()
        appConfigRepository.saveUserCodeVersion(BuildConfig.VERSION_CODE)
    }
}