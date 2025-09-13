package ru.sokolovromann.myshopping.carts

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CartWithProductsDao {

    @Transaction
    @Query("SELECT * FROM api_39_carts WHERE directory = :directory")
    fun observeByDirectory(directory: String): Flow<List<CartWithProductsEntity>>

    @Transaction
    @Query("SELECT * FROM api_39_carts WHERE id = :id")
    fun observeById(id: String): Flow<CartWithProductsEntity?>
}