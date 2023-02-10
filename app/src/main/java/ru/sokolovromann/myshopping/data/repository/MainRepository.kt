package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.*

interface MainRepository {

    suspend fun getMainPreferences(): Flow<MainPreferences>

    suspend fun getDefaultCurrency(): Flow<Currency>

    suspend fun getAppVersion14(): Flow<AppVersion14>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun addProduct(product: Product)

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun addAppOpenedAction(appOpenedAction: AppOpenedAction)

    suspend fun addCurrency(currency: Currency)

    suspend fun addTaxRate(taxRate: TaxRate)

    suspend fun addFontSize(fontSize: FontSize)

    suspend fun addFirstLetterUppercase(firstLetterUppercase: Boolean)

    suspend fun addShoppingListsMultiColumns(multiColumns: Boolean)

    suspend fun addShoppingListsSort(sort: Sort)

    suspend fun addDisplayCompleted(displayCompleted: DisplayCompleted)

    suspend fun addShoppingListsDisplayTotal(displayTotal: DisplayTotal)

    suspend fun addProductsMultiColumns(multiColumns: Boolean)

    suspend fun addProductsDisplayTotal(displayTotal: DisplayTotal)

    suspend fun addProductsEditCompleted(editCompleted: Boolean)

    suspend fun addProductsAddLastProduct(addLastProduct: Boolean)

    suspend fun addDisplayMoney(displayMoney: Boolean)

    suspend fun addScreenSize(screenSize: ScreenSize)
}