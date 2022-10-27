package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.Settings

interface SettingsRepository {

    suspend fun getSettings(): Flow<Settings>

    suspend fun displayProductsAutocompleteAll()

    suspend fun displayProductAutocompleteName()

    suspend fun tinyFontSizeSelected()

    suspend fun smallFontSizeSelected()

    suspend fun mediumFontSizeSelected()

    suspend fun largeFontSizeSelected()

    suspend fun hugeFontSizeSelected()

    suspend fun invertNightTheme()

    suspend fun invertDisplayMoney()

    suspend fun invertDisplayCurrencyToLeft()

    suspend fun invertFirstLetterUppercase()

    suspend fun invertShoppingListsMultiColumns()

    suspend fun invertProductsMultiColumns()

    suspend fun invertProductsEditCompleted()

    suspend fun invertProductsAddLastProduct()

    suspend fun hideProductsAutocomplete()
}