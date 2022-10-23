package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface PurchasesDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Insert(onConflict = REPLACE)
    fun insertShopping(shoppingEntity: ShoppingEntity)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToArchive(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToTrash(uid: String, lastModified: Long)
}