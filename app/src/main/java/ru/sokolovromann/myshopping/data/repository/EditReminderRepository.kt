package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.EditReminder

interface EditReminderRepository {

    suspend fun getEditReminder(uid: String?): Flow<EditReminder>

    suspend fun saveReminder(uid: String, reminder: Long, lastModified: Long)

    suspend fun deleteReminder(uid: String, lastModified: Long)
}