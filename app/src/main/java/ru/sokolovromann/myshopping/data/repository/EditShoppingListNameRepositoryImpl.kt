package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import javax.inject.Inject

class EditShoppingListNameRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) : EditShoppingListNameRepository {

    private val shoppingListDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getEditShoppingListName(
        uid: String?
    ): Flow<EditShoppingListName> = withContext(AppDispatchers.IO) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toEditShoppingListName(null, it)
                emit(value)
            }
        } else {
            shoppingListDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toEditShoppingListName(entity, appConfigEntity)
                }
            )
        }
    }

    override suspend fun saveShoppingListName(
        uid: String,
        name: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListDao.updateName(uid, name, lastModified)
    }
}