package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface ProductsDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 1, last_modified = :lastModified WHERE product_uid = :uid")
    fun completeProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 0, last_modified = :lastModified WHERE product_uid = :uid")
    fun activeProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET position = :position, last_modified = :lastModified WHERE product_uid = :uid")
    fun updateProductPosition(uid: String, position: Int, lastModified: Long)

    @Update
    fun updateProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET display = 0 WHERE shopping_uid = :shoppingUid")
    fun hideProducts(shoppingUid: String)

    @Query("UPDATE products SET display = 0 WHERE product_uid = :uid")
    fun hideProduct(uid: String)
}