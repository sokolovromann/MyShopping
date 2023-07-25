package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Dao
interface BackupDao {

    @Query("SELECT * FROM shoppings")
    fun getShoppings(): Flow<List<ShoppingEntity>>

    @Query("SELECT * FROM products")
    fun getProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM autocompletes")
    fun getAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("SELECT uid FROM shoppings WHERE reminder > 0")
    fun getReminderUids(): Flow<List<String>>

    @Insert
    fun addShoppings(entities: List<ShoppingEntity>)

    @Insert
    fun addProducts(entities: List<ProductEntity>)

    @Insert
    fun addAutocompletes(entities: List<AutocompleteEntity>)

    @Query("DELETE FROM shoppings")
    fun deleteShoppings()

    @Query("DELETE FROM products")
    fun deleteProducts()

    @Query("DELETE FROM autocompletes")
    fun deleteAutocompletes()
}