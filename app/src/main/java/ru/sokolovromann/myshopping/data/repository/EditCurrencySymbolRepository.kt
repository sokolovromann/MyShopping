package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.EditCurrencySymbol

interface EditCurrencySymbolRepository {

    suspend fun getEditCurrencySymbol(): Flow<EditCurrencySymbol>

    suspend fun editCurrencySymbol(symbol: String)
}