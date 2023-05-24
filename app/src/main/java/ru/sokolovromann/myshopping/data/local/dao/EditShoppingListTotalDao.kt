package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface EditShoppingListTotalDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE shoppings SET total = :total, total_formatted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingTotal(uid: String, total: Float, lastModified: Long)

    @Query("UPDATE shoppings SET total = 0, total_formatted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun deleteShoppingTotal(uid: String, lastModified: Long)
}