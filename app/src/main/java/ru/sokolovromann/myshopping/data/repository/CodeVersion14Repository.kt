package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.model.CodeVersion14
import ru.sokolovromann.myshopping.data.model.mapper.CodeVersion14Mapper
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOn
import javax.inject.Inject

class CodeVersion14Repository @Inject constructor(localDatasource: LocalDatasource) {

    private val codeVersion14Dao = localDatasource.getCodeVersion14Dao()
    private val appConfigDao = localDatasource.getAppConfigDao()
    private val resourcesDao = localDatasource.getResourcesDao()

    private val dispatcher = Dispatcher.IO

    fun getCodeVersion14(): Flow<CodeVersion14> {
        return appConfigDao.getCodeVersion14Preferences().map { codeVersion14PreferencesEntity ->
            CodeVersion14Mapper.toCodeVersion14(
                shoppingsCursor = codeVersion14Dao.getShoppingsCursor(),
                productsCursor = codeVersion14Dao.getProductsCursor(),
                autocompletesCursor = codeVersion14Dao.getAutocompletesCursor(),
                defaultAutocompleteNames = resourcesDao.getAutocompleteNames(),
                preferences = codeVersion14PreferencesEntity
            )
        }.flowOn(dispatcher)
    }
}