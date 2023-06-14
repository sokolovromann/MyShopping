package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface ProductsWidgetDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE products SET completed = 1, last_modified = :lastModified WHERE product_uid = :uid")
    fun completeProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 0, last_modified = :lastModified WHERE product_uid = :uid")
    fun activeProduct(uid: String, lastModified: Long)
}