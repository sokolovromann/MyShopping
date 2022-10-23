package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface MoveProductDao {

    @Transaction
    @Query("SELECT * FROM shoppings")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("UPDATE products SET shopping_uid = :shoppingUid, last_modified = :lastModified WHERE product_uid = :productUid")
    fun moveProduct(productUid: String, shoppingUid: String, lastModified: Long)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)
}