package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface TrashDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 1")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT position FROM shoppings")
    fun getShoppingsLastPosition(): Flow<Int?>

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToPurchases(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToArchive(uid: String, lastModified: Long)

    @Query("DELETE FROM shoppings WHERE uid = :uid")
    fun deleteShoppingList(uid: String)

    @Query("DELETE FROM products WHERE shopping_uid = :shoppingUid")
    fun deleteProducts(shoppingUid: String)
}