package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.SettingsDao
import ru.sokolovromann.myshopping.data.local.dao.SettingsPreferencesDao
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalPreferences
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.local.resources.SettingsResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao,
    private val preferencesDao: SettingsPreferencesDao,
    private val settingsResources: SettingsResources,
    private val autocompletesResources: AutocompletesResources,
    private val appVersion14LocalDatabase: AppVersion14LocalDatabase,
    private val appVersion14Preferences: AppVersion14LocalPreferences,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : SettingsRepository {

    override suspend fun getSettings(): Flow<Settings> = withContext(dispatchers.io) {
        return@withContext combine(
            flow = preferencesDao.getAppPreferences(),
            flow2 = settingsResources.getSettingsResources(),
            transform = { preferencesEntity, resourcesEntity ->
                mapping.toSettings(preferencesEntity, resourcesEntity)
            }
        )
    }

    override suspend fun getReminderUids(): Flow<List<String>> = withContext(dispatchers.io) {
        return@withContext settingsDao.getReminderUids()
    }

    override suspend fun getAppVersion14(): Flow<AppVersion14> = withContext(dispatchers.io) {
        return@withContext autocompletesResources.getDefaultAutocompleteNames().map {
            mapping.toAppVersion14(
                shoppingListsCursor = appVersion14LocalDatabase.getShoppings(),
                productsCursor = appVersion14LocalDatabase.getProducts(),
                autocompletesCursor = appVersion14LocalDatabase.getAutocompletes(),
                defaultAutocompleteNames = it,
                preferences = appVersion14Preferences.getAppVersion14Preferences()
            )
        }
    }

    override suspend fun addShoppingList(shoppingList: ShoppingList): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        settingsDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach {
            val productEntity = mapping.toProductEntity(it)
            settingsDao.insertProduct(productEntity)
        }
    }

    override suspend fun addAutocomplete(autocomplete: Autocomplete): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        settingsDao.insertAutocomplete(entity)
    }

    override suspend fun deleteAppData(): Result<Unit> = withContext(dispatchers.io) {
        settingsDao.deleteShoppings()
        settingsDao.deleteProducts()
        settingsDao.deleteAutocompletes()
        return@withContext Result.success(Unit)
    }

    override suspend fun displayCompletedPurchasesFirst(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        preferencesDao.displayCompletedPurchases(displayCompleted)
    }

    override suspend fun displayCompletedPurchasesLast(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        preferencesDao.displayCompletedPurchases(displayCompleted)
    }

    override suspend fun displayShoppingsProductsColumns(): Unit = withContext(dispatchers.io) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.COLUMNS)
        preferencesDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun displayShoppingsProductsRow(): Unit = withContext(dispatchers.io) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.ROW)
        preferencesDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun tinyFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.TINY)
        preferencesDao.saveFontSize(fontSize)
    }

    override suspend fun smallFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.SMALL)
        preferencesDao.saveFontSize(fontSize)
    }

    override suspend fun mediumFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.MEDIUM)
        preferencesDao.saveFontSize(fontSize)
    }

    override suspend fun largeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.LARGE)
        preferencesDao.saveFontSize(fontSize)
    }

    override suspend fun hugeFontSizeSelected(): Unit = withContext(dispatchers.io) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE)
        preferencesDao.saveFontSize(fontSize)
    }

    override suspend fun invertNightTheme(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertNightTheme()
    }

    override suspend fun invertDisplayMoney(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertDisplayMoney()
    }

    override suspend fun invertDisplayCurrencyToLeft(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertDisplayCurrencyToLeft()
    }

    override suspend fun invertEditProductAfterCompleted(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertEditProductAfterCompleted()
    }

    override suspend fun invertSaveProductToAutocompletes(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertSaveProductToAutocompletes()
    }

    override suspend fun invertDisplayDefaultAutocompletes(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertDisplayDefaultAutocompletes()
    }

    override suspend fun invertCompletedWithCheckbox(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertCompletedWithCheckbox()
    }

    override suspend fun invertEnterToSaveProduct(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertEnterToSaveProduct()
    }

    override suspend fun hideCompletedPurchases(): Unit = withContext(dispatchers.io) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        preferencesDao.displayCompletedPurchases(displayCompleted)
    }

    override suspend fun hideShoppingsProducts(): Unit = withContext(dispatchers.io) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.HIDE)
        preferencesDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun invertHighlightCheckbox(): Unit = withContext(dispatchers.io) {
        preferencesDao.invertHighlightCheckbox()
    }
}