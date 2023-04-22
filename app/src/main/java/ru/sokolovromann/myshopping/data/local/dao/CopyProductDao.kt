package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface CopyProductDao {

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getPurchases(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 1 AND deleted = 0")
    fun getArchive(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM products WHERE product_uid IN (:uids)")
    fun getProducts(uids: List<String>): Flow<List<ProductEntity>>

    @Query("SELECT position FROM products WHERE shopping_uid = :shoppingUid ORDER BY position DESC LIMIT 1")
    fun getProductsLastPosition(shoppingUid: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(productEntities: List<ProductEntity>)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)
}