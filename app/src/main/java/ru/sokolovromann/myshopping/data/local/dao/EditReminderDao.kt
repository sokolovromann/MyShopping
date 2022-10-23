package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface EditReminderDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE shoppings SET reminder = reminder, last_modified = last_modified WHERE uid = :uid")
    fun updateReminder(uid: String, reminder: Long, lastModified: Long)

    @Query("UPDATE shoppings SET reminder = 0, last_modified = last_modified WHERE uid = :uid")
    fun deleteReminder(uid: String, lastModified: Long)
}