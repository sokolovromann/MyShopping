package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import javax.inject.Inject

class EditReminderRepositoryImpl @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
): EditReminderRepository {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    override suspend fun getEditReminder(uid: String?): Flow<EditReminder> = withContext(
        AppDispatchers.IO) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().transform {
                val value = mapping.toEditReminder(null, it)
                emit(value)
            }
        } else {
            shoppingListsDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { entity, appConfigEntity ->
                    mapping.toEditReminder(entity, appConfigEntity)
                }
            )
        }
    }

    override suspend fun saveReminder(
        uid: String,
        reminder: Long,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.updateReminder(uid, reminder, lastModified)
    }

    override suspend fun deleteReminder(
        uid: String,
        lastModified: Long
    ): Unit = withContext(AppDispatchers.IO) {
        shoppingListsDao.deleteReminder(uid, lastModified)
    }
}