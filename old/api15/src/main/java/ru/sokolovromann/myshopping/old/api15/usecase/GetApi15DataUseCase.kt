package ru.sokolovromann.myshopping.old.api15.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.old.api15.datasource.Api15Dao
import ru.sokolovromann.myshopping.old.api15.datasource.DatasourceKey
import ru.sokolovromann.myshopping.old.api15.di.Api15DataStore
import ru.sokolovromann.myshopping.old.api15.model.Api15Data
import ru.sokolovromann.myshopping.old.api15.model.AppBuildConfig
import ru.sokolovromann.myshopping.old.api15.model.Autocomplete
import ru.sokolovromann.myshopping.old.api15.model.Product
import ru.sokolovromann.myshopping.old.api15.model.Shopping
import ru.sokolovromann.myshopping.old.api15.model.Suggestion
import ru.sokolovromann.myshopping.old.api15.model.SuggestionDetail
import ru.sokolovromann.myshopping.old.api15.model.UserPreferences
import kotlin.coroutines.CoroutineContext

class GetApi15DataUseCase @Inject constructor(
    private val api15Dao: Api15Dao,
    @Api15DataStore private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): Api15Data =
        withContext(dispatcher) {
            Api15Data(
                getShoppings(),
                getProducts(),
                getAutocompletes(),
                getSuggestions(),
                getSuggestionDetails(),
                getAppBuildConfig(),
                getUserPreferences()
            )
        }

    private fun getShoppings(): Collection<Shopping> =
        api15Dao.getShoppings().map { entity ->
            Shopping(
                entity.id,
                entity.position,
                entity.uid,
                entity.lastModified,
                entity.name,
                entity.reminder,
                entity.discount,
                entity.discountAsPercent,
                entity.discountProducts,
                entity.total,
                entity.totalFormatted,
                entity.budget,
                entity.budgetProducts,
                entity.archived,
                entity.deleted,
                entity.sortBy,
                entity.sortAscending,
                entity.sortFormatted,
                entity.pinned
            )
        }

    private fun getProducts(): Collection<Product> =
        api15Dao.getProducts().map { entity ->
            Product(
                entity.id,
                entity.position,
                entity.productUid,
                entity.shoppingUid,
                entity.lastModified,
                entity.name,
                entity.quantity,
                entity.quantitySymbol,
                entity.price,
                entity.discount,
                entity.discountAsPercent,
                entity.taxRate,
                entity.taxRateAsPercent,
                entity.total,
                entity.totalFormatted,
                entity.note,
                entity.manufacturer,
                entity.brand,
                entity.size,
                entity.color,
                entity.provider,
                entity.completed,
                entity.pinned
            )
        }

    private fun getAutocompletes(): Collection<Autocomplete> =
        api15Dao.getAutocompletes().map { entity ->
            Autocomplete(
                entity.id,
                entity.uid,
                entity.lastModified,
                entity.name,
                entity.quantity,
                entity.quantitySymbol,
                entity.price,
                entity.discount,
                entity.discountAsPercent,
                entity.taxRate,
                entity.taxRateAsPercent,
                entity.total,
                entity.manufacturer,
                entity.brand,
                entity.size,
                entity.color,
                entity.provider,
                entity.personal,
                entity.language
            )
        }

    private fun getSuggestions(): Collection<Suggestion> =
        api15Dao.getSuggestions().map { entity ->
            Suggestion(
                entity.uid,
                entity.directory,
                entity.created,
                entity.lastModified,
                entity.name,
                entity.used
            )
        }

    private fun getSuggestionDetails(): Collection<SuggestionDetail> =
        api15Dao.getSuggestionDetails().map { entity ->
            SuggestionDetail(
                entity.uid,
                entity.directory,
                entity.created,
                entity.lastModified,
                entity.type,
                entity.value,
                entity.valueParams,
                entity.used
            )
        }

    private suspend fun getAppBuildConfig(): AppBuildConfig =
        dataStore.data.map { preferences ->
            AppBuildConfig(
                appFirstTime = preferences[DatasourceKey.Build.appFirstTime],
                userCodeVersion = preferences[DatasourceKey.Build.userCodeVersion]
            )
        }.first()

    private suspend fun getUserPreferences(): UserPreferences =
        dataStore.data.map { preferences ->
            UserPreferences(
                nightTheme = preferences[DatasourceKey.User.appNightTheme],
                widgetNightTheme = preferences[DatasourceKey.User.widgetNightTheme],
                fontSize = preferences[DatasourceKey.User.appFontSize],
                widgetFontSize = preferences[DatasourceKey.User.widgetFontSize],
                shoppingsMultiColumns = preferences[DatasourceKey.User.shoppingsMultiColumns],
                shoppingsSortBy = preferences[DatasourceKey.User.shoppingsSortBy],
                shoppingsSortAscending = preferences[DatasourceKey.User.shoppingsSortAscending],
                shoppingsSortFormatted = preferences[DatasourceKey.User.shoppingsSortFormatted],
                productsMultiColumns = preferences[DatasourceKey.User.productsMultiColumns],
                displayCompleted = preferences[DatasourceKey.User.displayCompleted],
                widgetDisplayCompleted = preferences[DatasourceKey.User.widgetDisplayCompleted],
                strikethroughCompletedProducts = preferences[DatasourceKey.User.strikethroughCompletedProducts],
                displayTotal = preferences[DatasourceKey.User.displayTotal],
                displayLongTotal = preferences[DatasourceKey.User.displayLongTotal],
                displayOtherFields = preferences[DatasourceKey.User.displayOtherFields],
                coloredCheckbox = preferences[DatasourceKey.User.coloredCheckbox],
                displayShoppingsProducts = preferences[DatasourceKey.User.displayShoppingsProducts],
                purchasesSeparator = preferences[DatasourceKey.User.purchasesSeparator],
                editProductAfterCompleted = preferences[DatasourceKey.User.editProductAfterCompleted],
                lockProductElement = preferences[DatasourceKey.User.lockProductElement],
                completedWithCheckbox = preferences[DatasourceKey.User.completedWithCheckbox],
                enterToSaveProduct = preferences[DatasourceKey.User.enterToSaveProduct],
                displayDefaultAutocompletes = preferences[DatasourceKey.User.displayDefaultAutocompletes],
                maxAutocompletesNames = preferences[DatasourceKey.User.maxAutocompletesNames],
                maxAutocompletesQuantities = preferences[DatasourceKey.User.maxAutocompletesQuantities],
                maxAutocompletesMoneys = preferences[DatasourceKey.User.maxAutocompletesMoneys],
                maxAutocompletesOthers = preferences[DatasourceKey.User.maxAutocompletesOthers],
                saveProductToAutocompletes = preferences[DatasourceKey.User.saveProductToAutocompletes],
                displayMoney = preferences[DatasourceKey.User.displayMoney],
                currency = preferences[DatasourceKey.User.currency],
                displayCurrencyToLeft = preferences[DatasourceKey.User.displayCurrencyToLeft],
                taxRate = preferences[DatasourceKey.User.taxRate],
                taxRateAsPercent = preferences[DatasourceKey.User.taxRateAsPercent],
                minMoneyFractionDigits = preferences[DatasourceKey.User.minMoneyFractionDigits],
                minQuantityFractionDigits = preferences[DatasourceKey.User.minQuantityFractionDigits],
                maxMoneyFractionDigits = preferences[DatasourceKey.User.maxMoneyFractionDigits],
                maxQuantityFractionDigits = preferences[DatasourceKey.User.maxQuantityFractionDigits],
                automaticallyEmptyTrash = preferences[DatasourceKey.User.automaticallyEmptyTrash],
                displayListOfAutocompletes = preferences[DatasourceKey.User.displayListOfAutocompletes],
                afterSaveProduct = preferences[DatasourceKey.User.afterSaveProduct],
                afterProductCompleted = preferences[DatasourceKey.User.afterProductCompleted],
                afterAddShopping = preferences[DatasourceKey.User.afterAddShopping],
                afterShoppingCompleted = preferences[DatasourceKey.User.afterShoppingCompleted],
                displayEmptyShoppings = preferences[DatasourceKey.User.displayEmptyShoppings],
                swipeProductLeft = preferences[DatasourceKey.User.swipeProductLeft],
                swipeProductRight = preferences[DatasourceKey.User.swipeProductRight],
                swipeShoppingLeft = preferences[DatasourceKey.User.swipeShoppingLeft],
                swipeShoppingRight = preferences[DatasourceKey.User.swipeShoppingRight],
                archiveAsCompleted = preferences[DatasourceKey.User.archiveAsCompleted]
            )
        }.first()
}