package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Settings

interface SettingsRepository {

    suspend fun getSettings(): Flow<Settings>

    suspend fun displayCompletedPurchasesFirst()

    suspend fun displayCompletedPurchasesLast()

    suspend fun tinyFontSizeSelected()

    suspend fun smallFontSizeSelected()

    suspend fun mediumFontSizeSelected()

    suspend fun largeFontSizeSelected()

    suspend fun hugeFontSizeSelected()

    suspend fun invertNightTheme()

    suspend fun invertDisplayMoney()

    suspend fun invertDisplayCurrencyToLeft()

    suspend fun invertShoppingListsMultiColumns()

    suspend fun invertProductsMultiColumns()

    suspend fun invertEditProductAfterCompleted()

    suspend fun invertSaveProductToAutocompletes()

    suspend fun invertDisplayDefaultAutocompletes()

    suspend fun hideCompletedPurchases()
}