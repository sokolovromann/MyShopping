package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.model.CodeVersion14
import ru.sokolovromann.myshopping.data.model.mapper.CodeVersion14Mapper
import javax.inject.Inject

class CodeVersion14Repository @Inject constructor(localDatasource: LocalDatasource) {

    private val codeVersion14Dao = localDatasource.getCodeVersion14Dao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getCodeVersion14(): Flow<CodeVersion14> = withContext(dispatcher) {
        return@withContext appConfigDao.getCodeVersion14Preferences().map { codeVersion14PreferencesEntity ->
            CodeVersion14Mapper.toCodeVersion14(
                shoppingsCursor = codeVersion14Dao.getShoppingsCursor(),
                productsCursor = codeVersion14Dao.getProductsCursor(),
                autocompletesCursor = codeVersion14Dao.getAutocompletesCursor(),
                defaultAutocompleteNames = resourcesDao.getAutocompleteNames(),
                preferences = codeVersion14PreferencesEntity
            )
        }
    }
}