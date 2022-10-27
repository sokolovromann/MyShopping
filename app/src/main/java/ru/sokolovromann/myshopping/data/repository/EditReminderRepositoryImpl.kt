package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.EditReminderDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import javax.inject.Inject

class EditReminderRepositoryImpl @Inject constructor(
    private val reminderDao: EditReminderDao,
    private val preferencesDao: ProductsPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
): EditReminderRepository {

    override suspend fun getEditReminder(uid: String): Flow<EditReminder?> = withContext(dispatchers.io) {
        return@withContext reminderDao.getShoppingList(uid).combine(
            flow = preferencesDao.getProductsPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toEditReminder(entity, preferencesEntity)
            }
        )
    }

    override suspend fun saveReminder(
        uid: String,
        reminder: Long,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        reminderDao.updateReminder(uid, reminder, lastModified)
    }

    override suspend fun deleteReminder(
        uid: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        reminderDao.deleteReminder(uid, lastModified)
    }
}