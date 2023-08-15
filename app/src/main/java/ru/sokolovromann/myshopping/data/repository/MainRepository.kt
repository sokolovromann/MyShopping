package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.*
import java.text.DecimalFormat

interface MainRepository {

    suspend fun getAppConfig(): Flow<AppConfig>

    suspend fun getDefaultCurrency(): Flow<Currency>

    suspend fun getCodeVersion14(): Flow<CodeVersion14>

    suspend fun addShoppingList(shoppingList: ShoppingList)

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun addAppConfig(appConfig: AppConfig)

    suspend fun addMoneyDecimalFormat(decimalFormat: DecimalFormat)

    suspend fun addDisplayOtherFields(displayOtherFields: Boolean)
}