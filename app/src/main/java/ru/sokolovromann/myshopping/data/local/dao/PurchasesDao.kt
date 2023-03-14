package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface PurchasesDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT position FROM shoppings ORDER BY position DESC LIMIT 1")
    fun getShoppingsLastPosition(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopping(shoppingEntity: ShoppingEntity)

    @Query("UPDATE shoppings SET position = :position, last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingPosition(uid: String, position: Int, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToArchive(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToTrash(uid: String, lastModified: Long)
}