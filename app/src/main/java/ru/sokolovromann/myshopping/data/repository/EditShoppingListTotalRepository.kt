package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.Money

interface EditShoppingListTotalRepository {

    suspend fun getEditShoppingListTotal(uid: String?): Flow<EditShoppingListTotal>

    suspend fun saveShoppingListTotal(uid: String, total: Money, lastModified: Long)

    suspend fun deleteShoppingListTotal(uid: String, lastModified: Long)
}