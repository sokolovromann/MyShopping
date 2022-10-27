package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName

interface EditShoppingListNameRepository {

    suspend fun getEditShoppingListName(uid: String): Flow<EditShoppingListName?>

    suspend fun saveShoppingListName(uid: String, name: String, lastModified: Long)
}