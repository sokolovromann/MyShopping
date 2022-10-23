package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface CopyProductDao {

    @Transaction
    @Query("SELECT * FROM shoppings")
    fun getShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM products WHERE product_uid = :uid")
    fun getProduct(uid: String): Flow<ProductEntity?>

    @Insert(onConflict = REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)
}