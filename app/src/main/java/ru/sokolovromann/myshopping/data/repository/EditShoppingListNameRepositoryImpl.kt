package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.dao.EditShoppingListNameDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsPreferencesDao
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import javax.inject.Inject

class EditShoppingListNameRepositoryImpl @Inject constructor(
    private val shoppingListDao: EditShoppingListNameDao,
    private val preferencesDao: ProductsPreferencesDao,
    private val mapping: RepositoryMapping,
    private val dispatchers: AppDispatchers
) : EditShoppingListNameRepository {

    override suspend fun getEditShoppingListName(
        uid: String
    ): Flow<EditShoppingListName?> = withContext(dispatchers.io) {
        return@withContext shoppingListDao.getShoppingList(uid).combine(
            flow = preferencesDao.getProductsPreferences(),
            transform = { entity, preferencesEntity ->
                if (entity == null) {
                    return@combine null
                }

                mapping.toEditShoppingListName(entity, preferencesEntity)
            }
        )
    }

    override suspend fun saveShoppingListName(
        uid: String,
        name: String,
        lastModified: Long
    ): Unit = withContext(dispatchers.io) {
        shoppingListDao.updateShoppingName(uid, name, lastModified)
    }
}