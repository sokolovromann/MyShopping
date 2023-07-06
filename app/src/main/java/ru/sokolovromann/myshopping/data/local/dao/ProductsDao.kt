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

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToPurchases(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToArchive(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun moveShoppingToTrash(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 1, last_modified = :lastModified WHERE product_uid = :uid")
    fun completeProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 0, last_modified = :lastModified WHERE product_uid = :uid")
    fun activeProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET pinned = 1, last_modified = :lastModified WHERE product_uid = :uid")
    fun pinProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET pinned = 0, last_modified = :lastModified WHERE product_uid = :uid")
    fun unpinProduct(uid: String, lastModified: Long)

    @Query("UPDATE products SET position = :position, last_modified = :lastModified WHERE product_uid = :uid")
    fun updateProductPosition(uid: String, position: Int, lastModified: Long)

    @Update
    fun updateProducts(products: List<ProductEntity>)

    @Query("UPDATE shoppings SET sort_by = :sortBy, last_modified = :lastModified WHERE uid = :shoppingUid")
    fun sortProductsBy(shoppingUid: String, sortBy: String, lastModified: Long)

    @Query("UPDATE shoppings SET sort_ascending = :sortAscending, last_modified = :lastModified WHERE uid = :shoppingUid")
    fun sortProductsAscending(shoppingUid: String, sortAscending: Boolean, lastModified: Long)

    @Query("UPDATE shoppings SET sort_formatted = 1, last_modified = :lastModified WHERE uid = :shoppingUid")
    fun enableProductsAutomaticSorting(shoppingUid: String, lastModified: Long)

    @Query("UPDATE shoppings SET sort_by = :sortBy, sort_ascending = :sortAscending, sort_formatted = 0, last_modified = :lastModified WHERE uid = :shoppingUid")
    fun disableProductsAutomaticSorting(shoppingUid: String, sortBy: String, sortAscending: Boolean, lastModified: Long)

    @Query("DELETE FROM products WHERE product_uid = :uid")
    fun deleteProduct(uid: String)
}