package ru.sokolovromann.myshopping.core.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.data.model.CartWithProductsEntity

@Dao
interface CartsDao {

    @Transaction
    @Query("SELECT * FROM api39_carts WHERE directory = :directory")
    fun observeCartsWithProducts(directory: String): Flow<Collection<CartWithProductsEntity>>

    @Transaction
    @Query("SELECT * FROM api39_carts WHERE uid = :uid")
    fun observeCartWithProducts(uid: String): Flow<CartWithProductsEntity?>

    @Query("SELECT * FROM api39_carts WHERE uid = :uid")
    fun getCart(uid: String): CartEntity?

    @Query("SELECT position FROM api39_carts ORDER BY CAST(position as INT) DESC LIMIT 1")
    fun getCurrentCartPosition(): String?

    @Insert(onConflict = REPLACE)
    fun insertCarts(carts: Collection<CartEntity>)

    @Query("DELETE FROM api39_carts WHERE directory = :directory")
    fun deleteCarts(directory: String)

    @Query("DELETE FROM api39_carts WHERE uid IN(:uids)")
    fun deleteCarts(uids: Collection<String>)

    @Query("DELETE FROM api39_carts")
    fun clearCarts()
}