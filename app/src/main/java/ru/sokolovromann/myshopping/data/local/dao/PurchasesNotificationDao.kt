package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface PurchasesNotificationDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE reminder > 0")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE shoppings SET reminder = 0, last_modified = :lastModified WHERE uid = :uid")
    fun deleteReminder(uid: String, lastModified: Long)
}