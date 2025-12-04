package ru.sokolovromann.myshopping.data39.products

import androidx.room.Dao
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductsRoomDao {

    @Query("SELECT * FROM api39_products")
    fun getAll(): List<ProductRoomEntity>

    @Query("SELECT * FROM api39_products WHERE uid = :uid")
    fun get(uid: String): ProductRoomEntity

    @Update(onConflict = REPLACE)
    fun updateAll(products: List<ProductRoomEntity>)

    @Update(onConflict = REPLACE)
    fun update(product: ProductRoomEntity)

    @Query("DELETE FROM api39_products WHERE uid IN(:uids)")
    fun deleteAll(uids: List<String>)

    @Query("DELETE FROM api39_products WHERE uid = :uid")
    fun delete(uid: String)

    @Query("DELETE FROM api39_products")
    fun clear()
}