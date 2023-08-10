package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.MainDao
import ru.sokolovromann.myshopping.data.local.datasource.CodeVersion14LocalDatabase
import ru.sokolovromann.myshopping.data.local.resources.AutocompletesResources
import ru.sokolovromann.myshopping.data.local.resources.MainResources
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val mainDao: MainDao,
    private val appConfigDao: AppConfigDao,
    private val mainResources: MainResources,
    private val autocompletesResources: AutocompletesResources,
    private val codeVersion14LocalDatabase: CodeVersion14LocalDatabase,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : MainRepository {

    override suspend fun getAppPreferences(): Flow<AppPreferences> = withContext(dispatchers.io) {
        return@withContext appConfigDao.getAppConfig().transform {
            val value = mapping.toAppPreferences(it)
            emit(value)
        }
    }

    override suspend fun getDefaultCurrency(): Flow<Currency> = withContext(dispatchers.io) {
        return@withContext mainResources.getCurrencyResources().transform {
            val value = mapping.toCurrency(it.defaultCurrency, it.displayDefaultCurrencyToLeft)
            emit(value)
        }
    }

    override suspend fun getCodeVersion14(): Flow<CodeVersion14> = withContext(dispatchers.io) {
        return@withContext autocompletesResources.getDefaultAutocompleteNames().combine(
            flow = appConfigDao.getCodeVersion14Preferences(),
            transform = { names, preferences ->
                mapping.toCodeVersion14(
                    shoppingListsCursor = codeVersion14LocalDatabase.getShoppings(),
                    productsCursor = codeVersion14LocalDatabase.getProducts(),
                    autocompletesCursor = codeVersion14LocalDatabase.getAutocompletes(),
                    defaultAutocompleteNames = names,
                    preferences = preferences
                )
            }
        )
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