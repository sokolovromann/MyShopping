package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity

@Dao
interface AddEditProductDao {

    @Query("SELECT * FROM products WHERE name LIKE '%' || :search || '%'")
    fun getProducts(search: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE product_uid = :uid")
    fun getProduct(uid: String): Flow<ProductEntity?>

    @Query("SELECT * FROM autocompletes WHERE name LIKE '%' || :search || '%'")
    fun getAutocompletes(search: String): Flow<List<AutocompleteEntity>>

    @Query("SELECT position FROM products WHERE shopping_uid = :shoppingUid ORDER BY position DESC LIMIT 1")
    fun getProductsLastPosition(shoppingUid: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateShoppingLastModified(uid: String, lastModified: Long)
}