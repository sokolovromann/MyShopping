package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface EditShoppingListNameDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE shoppings SET name = :name, last_modified = last_modified WHERE uid = :uid")
    fun updateShoppingName(uid: String, name: String, lastModified: Long)
}