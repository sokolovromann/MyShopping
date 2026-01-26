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
import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.suggestions.AddSuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailValue
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDirectory
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfig
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsDefaults
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsPreInstalled
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetailsInfo
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions
import ru.sokolovromann.myshopping.manager.Api15Manager
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
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DecimalExtensions.toDecimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams
import ru.sokolovromann.myshopping.utils.math.DiscountType
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
    private val api15Manager: Api15Manager,
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
            .map { toSuggestion(SuggestionDirectory.PreInstalled, it, 0) }
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
        listOf(
            viewModelScope.async(dispatcher) { migrateApi15Autocompletes() },
            viewModelScope.async(dispatcher) { migrateApi15AutocompletesConfig() }
        ).awaitAll()

        appConfigRepository.saveUserCodeVersion(BuildConfig.VERSION_CODE)
    }

    private suspend fun migrateApi15Autocompletes() {
        val suggestionsWithDetails = api15Manager.getAutocompletes()
            .mapKeys { (key, _) ->
                val used = api15Manager.countAutocompleteNames(key)
                toSuggestion(SuggestionDirectory.Personal, key, used)
            }
            .mapValues { (key, value) -> toDetails(key.uid, value) }

        val suggestions = suggestionsWithDetails.keys
        suggestionsManager.addSuggestions(suggestions)

        val details = suggestionsWithDetails.flatMap { it.value }
        suggestionsManager.addDetails(details)
    }

    private suspend fun migrateApi15AutocompletesConfig() {
        val api15Config = api15Manager.getAutocompletesConfig()

        val addSuggestionWithDetails = if (api15Config.saveProductToAutocompletes == null) {
            SuggestionsDefaults.ADD
        } else {
            if (api15Config.saveProductToAutocompletes) {
                AddSuggestionWithDetails.SuggestionAndDetails
            } else {
                AddSuggestionWithDetails.DoNotAdd
            }
        }

        val takeSuggestion = if (api15Config.maxAutocompletesNames == null) {
            SuggestionsDefaults.TAKE_SUGGESTIONS
        } else {
            if (api15Config.maxAutocompletesNames == 0) {
                TakeSuggestions.DoNotTake
            } else if (api15Config.maxAutocompletesNames <= 5) {
                TakeSuggestions.Five
            } else {
                TakeSuggestions.Ten
            }
        }

        fun toTakeDetails(max: Int): TakeSuggestionDetails = when (max) {
            0 -> TakeSuggestionDetails.DoNotTake
            1 -> TakeSuggestionDetails.One
            2, 3 -> TakeSuggestionDetails.Three
            4, 5 -> TakeSuggestionDetails.Five
            else -> TakeSuggestionDetails.Ten
        }
        val takeDetails = TakeSuggestionDetailsInfo(
            descriptions = toTakeDetails(api15Config.maxAutocompletesOthers ?: 0),
            quantities = toTakeDetails(api15Config.maxAutocompletesQuantities ?: 0),
            money = toTakeDetails(api15Config.maxAutocompletesMoneys ?: 0)
        )

        val config = SuggestionsConfig(
            preInstalled = SuggestionsPreInstalled.DoNotAdd,
            viewMode = SuggestionsDefaults.VIEW_MODE,
            sort = SuggestionsDefaults.SORT,
            add = addSuggestionWithDetails,
            takeSuggestions = takeSuggestion,
            takeDetails = takeDetails
        )
        suggestionsManager.addConfig(config)
    }

    private fun toSuggestion(directory: SuggestionDirectory, name: String, used: Int): Suggestion {
        val currentDateTime = DateTimeAlias.getCurrent()
        return Suggestion(
            uid = UID.createRandom(),
            directory = directory,
            created = currentDateTime,
            lastModified = currentDateTime,
            name = name,
            used = used
        )
    }

    private fun toDetails(
        directory: UID,
        autocompletes: List<Api15AutocompleteEntity>
    ): Collection<SuggestionDetail> {
        return mutableListOf<SuggestionDetail>().apply {
            val manufacturers = autocompletes.map {
                val value = createStringSuggestionDetailValue(directory, it.manufacturer)
                SuggestionDetail.Manufacturer(value)
            }
            addAll(manufacturers)

            val brands = autocompletes.map {
                val value = createStringSuggestionDetailValue(directory, it.brand)
                SuggestionDetail.Brand(value)
            }
            addAll(brands)

            val sizes = autocompletes.map {
                val value = createStringSuggestionDetailValue(directory, it.size)
                SuggestionDetail.Size(value)
            }
            addAll(sizes)

            val colors = autocompletes.map {
                val value = createStringSuggestionDetailValue(directory, it.color)
                SuggestionDetail.Color(value)
            }
            addAll(colors)

            val quantities = autocompletes.map {
                val value = SuggestionDetailValue(
                    uid = UID.createRandom(),
                    directory = directory,
                    created = DateTimeAlias.getCurrent(),
                    data = DecimalWithParams(it.quantity.toDecimal(), it.quantitySymbol)
                )
                SuggestionDetail.Quantity(value)
            }
            addAll(quantities)

            val unitPrices = autocompletes.map {
                val value = createDecimalSuggestionDetailValue(directory, it.price)
                SuggestionDetail.UnitPrice(value)
            }
            addAll(unitPrices)

            val discounts = autocompletes.map {
                val type: DiscountType = if (it.discountAsPercent) {
                    DiscountType.Percent
                } else {
                    DiscountType.Money
                }
                val value = SuggestionDetailValue(
                    uid = UID.createRandom(),
                    directory = directory,
                    created = DateTimeAlias.getCurrent(),
                    data = DecimalWithParams(it.discount.toDecimal(), type)
                )
                SuggestionDetail.Discount(value)
            }
            addAll(discounts)

            val taxRates = autocompletes.map {
                val value = createDecimalSuggestionDetailValue(directory, it.taxRate)
                SuggestionDetail.TaxRate(value)
            }
            addAll(taxRates)

            val costs = autocompletes.map {
                val value = createDecimalSuggestionDetailValue(directory, it.total)
                SuggestionDetail.Cost(value)
            }
            addAll(costs)
        }
    }

    private fun createStringSuggestionDetailValue(
        directory: UID,
        data: String
    ): SuggestionDetailValue<String> {
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = DateTimeAlias.getCurrent(),
            data = data
        )
    }

    private fun createDecimalSuggestionDetailValue(
        directory: UID,
        data: Float
    ): SuggestionDetailValue<Decimal> {
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = DateTimeAlias.getCurrent(),
            data = data.toDecimal()
        )
    }
}