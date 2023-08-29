package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : MainRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val codeVersion14Dao = localDatasource.getCodeVersion14Dao()
    private val resourcesDao = localDatasource.getResourcesDao()

    override suspend fun getAppConfig(): Flow<AppConfig> = withContext(AppDispatchers.IO) {
        return@withContext appConfigDao.getAppConfig().transform {
            val value = mapping.toAppConfig(it)
            emit(value)
        }
    }

    override suspend fun getDefaultCurrency(): Flow<Currency> = withContext(AppDispatchers.IO) {
        val currency = resourcesDao.getCurrency()
        val value = mapping.toCurrency(currency.defaultCurrency, currency.displayDefaultCurrencyToLeft)
        return@withContext flowOf(value)
    }

    override suspend fun getCodeVersion14(): Flow<CodeVersion14> = withContext(AppDispatchers.IO) {
        return@withContext appConfigDao.getCodeVersion14Preferences().transform {
            mapping.toCodeVersion14(
                shoppingListsCursor = codeVersion14Dao.getShoppingsCursor(),
                productsCursor = codeVersion14Dao.getProductsCursor(),
                autocompletesCursor = codeVersion14Dao.getAutocompletesCursor(),
                defaultAutocompleteNames = resourcesDao.getAutocompleteNames(),
                preferences = it
            )
        }
    }

    override suspend fun addShoppingList(
        shoppingList: ShoppingList
    ): Unit = withContext(AppDispatchers.IO) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach {
            val productEntity = mapping.toProductEntity(it)
            productsDao.insertProduct(productEntity)
        }
    }

    override suspend fun addAutocomplete(
        autocomplete: Autocomplete
    ): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompletesDao.insertAutocomplete(entity)
    }

    override suspend fun addAppConfig(
        appConfig: AppConfig
    ): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAppConfigEntity(appConfig)
        appConfigDao.saveAppConfig(entity)
    }
}