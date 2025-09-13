package ru.sokolovromann.myshopping.products

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface ProductsDao {

    @Query("SELECT * FROM api_39_products WHERE directory = :directory")
    fun getByDirectory(directory: String): List<ProductEntity>

    @Query("SELECT * FROM api_39_products WHERE id = :id")
    fun getById(id: String): ProductEntity?

    @Insert(onConflict = REPLACE)
    fun insert(products: Set<ProductEntity>)

    @Query("DELETE FROM api_39_products WHERE directory = :directory")
    fun deleteByDirectory(directory: String)

    @Query("DELETE FROM api_39_products WHERE id IN(:ids)")
    fun deleteByIds(ids: Set<String>)

    @Query("DELETE FROM api_39_products")
    fun clear()
}