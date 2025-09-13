package ru.sokolovromann.myshopping.carts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface CartsDao {

    @Query("SELECT * FROM api_39_carts WHERE directory = :directory")
    fun getByDirectory(directory: String): List<CartEntity>

    @Query("SELECT * FROM api_39_carts WHERE id = :id")
    fun getById(id: String): CartEntity?

    @Insert(onConflict = REPLACE)
    fun insert(carts: Set<CartEntity>)

    @Query("DELETE FROM api_39_carts WHERE directory = :directory")
    fun deleteByDirectory(directory: String)

    @Query("DELETE FROM api_39_carts WHERE id IN(:ids)")
    fun deleteByIds(ids: Set<String>)

    @Query("DELETE FROM api_39_carts")
    fun clear()
}