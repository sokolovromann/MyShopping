package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.MainDao
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppVersion14LocalPreferences
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val mainDao: MainDao,
    private val appConfigDao: AppConfigDao,
    private val mainResources: MainResources,
    private val autocompletesResources: AutocompletesResources,
    private val appVersion14LocalDatabase: AppVersion14LocalDatabase,
    private val appVersion14Preferences: AppVersion14LocalPreferences,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MainRepository {

    override suspend fun getAppPreferences(): Flow<AppPreferences> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().transform {
            val value = mapping.toAppPreferences(
                it,
                appVersion14Preferences.isMigrateFromAppVersion14()
            )
            emit(value)
        }
    }

    override suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatchers.io) {
        return@withContext mainResources.getCurrencyResources().transform {
            val value = mapping.toCurrency(it.defaultCurrency, it.displayDefaultCurrencyToLeft)
            emit(value)
        }
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

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(dispatchers.io) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        mainDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach {
            val productEntity = mapping.toProductEntity(it)
            mainDao.insertProduct(productEntity)
        }
    }

    override suspend fun addAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        mainDao.insertAutocomplete(entity)
    }

    override suspend fun addPreferences(
        appPreferences: AppPreferences
    ): Unit = withContext(dispatchers.io) {
        val entity = mapping.toAppConfigEntity(appPreferences)
        appConfigDao.saveAppConfig(entity)
    }
}