package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface ArchiveDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 1 AND deleted = 0")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT position FROM shoppings")
    fun getShoppingsLastPosition(): Flow<Int?>

    @Update
    fun updateShoppings(shoppingEntities: List<ShoppingEntity>)

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToPurchases(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToTrash(uid: String, lastModified: Long)
}