package ru.sokolovromann.myshopping.data39.carts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CartsRoomDao {

    @Transaction
    @Query("SELECT * FROM api39_carts")
    fun observeAll(): Flow<List<CartWithProductsRoomEntity>>

    @Transaction
    @Query("SELECT * FROM api39_carts WHERE uid = :uid")
    fun observe(uid: String): Flow<CartWithProductsRoomEntity>

    @Transaction
    @Query("SELECT * FROM api39_carts")
    fun getAll(): List<CartWithProductsRoomEntity>

    @Transaction
    @Query("SELECT * FROM api39_carts WHERE uid = :uid")
    fun get(uid: String): CartWithProductsRoomEntity

    @Insert(onConflict = REPLACE)
    fun insertAll(carts: List<CartRoomEntity>)

    @Insert(onConflict = REPLACE)
    fun insert(cart: CartRoomEntity)

    @Query("DELETE FROM api39_carts WHERE uid IN(:uids)")
    fun deleteAll(uids: List<String>)

    @Query("DELETE FROM api39_carts WHERE uid = :uid")
    fun delete(uid: String)

    @Query("DELETE FROM api39_carts")
    fun clear()
}