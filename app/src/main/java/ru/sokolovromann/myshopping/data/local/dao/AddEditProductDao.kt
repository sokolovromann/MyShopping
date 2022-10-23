package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity

@Dao
interface AddEditProductDao {

    @Query("SELECT * FROM products WHERE product_uid = :uid")
    fun getProduct(uid: String): Flow<ProductEntity?>

    @Query("SELECT * FROM autocompletes WHERE name LIKE '%' || :search || '%'")
    fun getAutocompletes(search: String): Flow<List<AutocompleteEntity>>

    @Insert(onConflict = REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)

    @Query("UPDATE shoppings SET last_modified = last_modified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)
}