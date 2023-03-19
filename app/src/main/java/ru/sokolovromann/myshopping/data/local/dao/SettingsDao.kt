package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Dao
interface SettingsDao {

    @Query("SELECT uid FROM shoppings WHERE reminder > 0")
    fun getReminderUids(): Flow<List<String>>

    @Query("DELETE FROM shoppings")
    fun deleteShoppings()

    @Query("DELETE FROM products")
    fun deleteProducts()

    @Query("DELETE FROM autocompletes")
    fun deleteAutocompletes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopping(shoppingEntity: ShoppingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)
}