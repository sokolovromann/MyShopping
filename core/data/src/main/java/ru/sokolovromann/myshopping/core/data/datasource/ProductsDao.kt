package ru.sokolovromann.myshopping.core.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.sokolovromann.myshopping.core.data.model.ProductEntity

@Dao
interface ProductsDao {

    @Query("SELECT * FROM api39_products WHERE uid = :uid")
    fun getProduct(uid: String): ProductEntity?

    @Query("SELECT position FROM api39_products ORDER BY CAST(position AS INT) DESC LIMIT 1")
    fun getCurrentProductPosition(): String?

    @Insert(onConflict = REPLACE)
    fun insertProducts(products: Collection<ProductEntity>)

    @Query("DELETE FROM api39_products WHERE directory = :directory")
    fun deleteProducts(directory: String)

    @Query("DELETE FROM api39_products WHERE uid IN(:uids)")
    fun deleteProducts(uids: Collection<String>)

    @Query("DELETE FROM api39_products")
    fun clearProducts()
}