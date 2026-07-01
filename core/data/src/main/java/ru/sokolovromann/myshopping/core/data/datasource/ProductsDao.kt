package ru.sokolovromann.myshopping.core.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import ru.sokolovromann.myshopping.core.data.model.ProductEntity

@Dao
interface ProductsDao {

    @Query("SELECT * FROM products WHERE uid = :uid")
    fun getProduct(uid: String): ProductEntity?

    @Query("SELECT position FROM products ORDER BY CAST(position AS INT) DESC LIMIT 1")
    fun getCurrentProductPosition(): String?

    @Insert(onConflict = REPLACE)
    fun insertProducts(products: Collection<ProductEntity>)

    @Query("DELETE FROM products WHERE directory = :directory")
    fun deleteProducts(directory: String)

    @Query("DELETE FROM products WHERE uid IN(:uids)")
    fun deleteProducts(uids: Collection<String>)

    @Query("DELETE FROM products")
    fun clearProducts()
}