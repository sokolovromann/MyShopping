package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.*
import java.text.DecimalFormat
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : SettingsRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val autocompletesDao = localDatasource.getAutocompletesDao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val codeVersion14Dao = localDatasource.getCodeVersion14Dao()
    private val resourcesDao = localDatasource.getResourcesDao()

    override suspend fun getSettings(): Flow<Settings> = withContext(AppDispatchers.IO) {
        return@withContext appConfigDao.getAppConfig().map {
            val settings = resourcesDao.getSettings()
            mapping.toSettings(it, settings)
        }
    }

    override suspend fun getReminderUids(): Flow<List<String>> = withContext(AppDispatchers.IO) {
        return@withContext shoppingListsDao.getReminderUids()
    }

    override suspend fun getCodeVersion14(): Flow<CodeVersion14> = withContext(AppDispatchers.IO) {
        return@withContext appConfigDao.getCodeVersion14Preferences().map {
            mapping.toCodeVersion14(
                shoppingListsCursor = codeVersion14Dao.getShoppingsCursor(),
                productsCursor = codeVersion14Dao.getProductsCursor(),
                autocompletesCursor = codeVersion14Dao.getAutocompletesCursor(),
                defaultAutocompleteNames = resourcesDao.getAutocompleteNames(),
                preferences = it
            )
        }
    }

    override suspend fun addShoppingList(shoppingList: ShoppingList): Unit = withContext(AppDispatchers.IO) {
        val shoppingEntity = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shoppingEntity)

        shoppingList.products.forEach {
            val productEntity = mapping.toProductEntity(it)
            productsDao.insertProduct(productEntity)
        }
    }

    override suspend fun addAutocomplete(autocomplete: Autocomplete): Unit = withContext(AppDispatchers.IO) {
        val entity = mapping.toAutocompleteEntity(autocomplete)
        autocompletesDao.insertAutocomplete(entity)
    }

    override suspend fun deleteAppData(): Result<Unit> = withContext(AppDispatchers.IO) {
        shoppingListsDao.deleteAllShoppings()
        productsDao.deleteAllProducts()
        autocompletesDao.deleteAllAutocompletes()
        return@withContext Result.success(Unit)
    }

    override suspend fun displayCompletedPurchasesFirst(): Unit = withContext(AppDispatchers.IO) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.FIRST)
        appConfigDao.displayCompleted(displayCompleted)
    }

    override suspend fun displayCompletedPurchasesLast(): Unit = withContext(AppDispatchers.IO) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.LAST)
        appConfigDao.displayCompleted(displayCompleted)
    }

    override suspend fun displayShoppingsProductsColumns(): Unit = withContext(AppDispatchers.IO) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.COLUMNS)
        appConfigDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun displayShoppingsProductsRow(): Unit = withContext(AppDispatchers.IO) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.ROW)
        appConfigDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun smallFontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.SMALL)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun mediumFontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.MEDIUM)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun largeFontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.LARGE)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun hugeFontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun huge2FontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE_2)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun huge3FontSizeSelected(): Unit = withContext(AppDispatchers.IO) {
        val fontSize = mapping.toFontSizeName(FontSize.HUGE_3)
        appConfigDao.saveFontSize(fontSize)
    }

    override suspend fun invertNightTheme(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertNightTheme(
            valueIfNull = !UserPreferencesDefaults.NIGHT_THEME
        )
    }

    override suspend fun invertDisplayMoney(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertDisplayMoney(
            valueIfNull = !UserPreferencesDefaults.DISPLAY_MONEY
        )
    }

    override suspend fun invertDisplayCurrencyToLeft(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertDisplayCurrencyToLeft(
            valueIfNull = !UserPreferencesDefaults.CURRENCY.displayToLeft
        )
    }

    override suspend fun invertEditProductAfterCompleted(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertEditProductAfterCompleted(
            valueIfNull = !UserPreferencesDefaults.EDIT_PRODUCT_AFTER_COMPLETED
        )
    }

    override suspend fun invertSaveProductToAutocompletes(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertSaveProductToAutocompletes(
            valueIfNull = !UserPreferencesDefaults.SAVE_PRODUCT_TO_AUTOCOMPLETES
        )
    }

    override suspend fun invertDisplayDefaultAutocompletes(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertDisplayDefaultAutocompletes(
            valueIfNull = !UserPreferencesDefaults.DISPLAY_DEFAULT_AUTOCOMPLETES
        )
    }

    override suspend fun invertCompletedWithCheckbox(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertCompletedWithCheckbox(
            valueIfNull = !UserPreferencesDefaults.COMPLETED_WITH_CHECKBOX
        )
    }

    override suspend fun invertEnterToSaveProduct(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertEnterToSaveProduct(
            valueIfNull = !UserPreferencesDefaults.ENTER_TO_SAVE_PRODUCTS
        )
    }

    override suspend fun hideCompletedPurchases(): Unit = withContext(AppDispatchers.IO) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.HIDE)
        appConfigDao.displayCompleted(displayCompleted)
    }

    override suspend fun hideShoppingsProducts(): Unit = withContext(AppDispatchers.IO) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.HIDE)
        appConfigDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun hideShoppingsProductsIfHasTitle(): Unit = withContext(AppDispatchers.IO) {
        val displayProducts = mapping.toDisplayProductsName(DisplayProducts.HIDE_IF_HAS_TITLE)
        appConfigDao.displayShoppingsProducts(displayProducts)
    }

    override suspend fun invertColoredCheckbox(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertColoredCheckbox(
            valueIfNull = !UserPreferencesDefaults.COLORED_CHECKBOX
        )
    }

    override suspend fun invertDisplayOtherFields(): Unit = withContext(AppDispatchers.IO) {
        appConfigDao.invertDisplayOtherFields(
            valueIfNull = !UserPreferencesDefaults.DISPLAY_OTHER_FIELDS
        )
    }

    override suspend fun noSplitCompletedPurchases(): Unit = withContext(AppDispatchers.IO) {
        val displayCompleted = mapping.toDisplayCompletedName(DisplayCompleted.NO_SPLIT)
        appConfigDao.displayCompleted(displayCompleted)
    }

    override suspend fun saveMoneyFractionDigits(decimalFormat: DecimalFormat): Unit = withContext(AppDispatchers.IO) {
        val min = mapping.toMinMoneyFractionDigits(decimalFormat)
        appConfigDao.saveMinMoneyFractionDigits(min)

        val max = mapping.toMaxMoneyFractionDigits(decimalFormat)
        appConfigDao.saveMaxMoneyFractionDigits(max)
    }
}