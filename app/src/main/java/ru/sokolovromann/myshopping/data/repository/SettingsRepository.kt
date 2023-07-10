package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.*

interface SettingsRepository {

    suspend fun getSettings(): Flow<Settings>

    suspend fun getReminderUids(): Flow<List<String>>

    suspend fun getAppVersion14(): Flow<AppVersion14>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun deleteAppData(): Result<Unit>

    suspend fun displayCompletedPurchasesFirst()

    suspend fun displayCompletedPurchasesLast()

    suspend fun displayShoppingsProductsColumns()

    suspend fun displayShoppingsProductsRow()

    suspend fun smallFontSizeSelected()

    suspend fun mediumFontSizeSelected()

    suspend fun largeFontSizeSelected()

    suspend fun hugeFontSizeSelected()

    suspend fun huge2FontSizeSelected()

    suspend fun huge3FontSizeSelected()

    suspend fun invertNightTheme()

    suspend fun invertDisplayMoney()

    suspend fun invertDisplayCurrencyToLeft()

    suspend fun invertEditProductAfterCompleted()

    suspend fun invertSaveProductToAutocompletes()

    suspend fun invertDisplayDefaultAutocompletes()

    suspend fun invertCompletedWithCheckbox()

    suspend fun invertEnterToSaveProduct()

    suspend fun hideCompletedPurchases()

    suspend fun hideShoppingsProducts()

    suspend fun invertHighlightCheckbox()
}