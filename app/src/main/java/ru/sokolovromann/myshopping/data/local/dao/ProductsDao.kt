package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity

@Dao
interface ProductsDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE product_uid IN (:productUids)")
    fun getProducts(productUids: List<String>): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :search || '%'")
    fun searchProductsLikeName(search: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE product_uid = :productUid")
    fun getProduct(productUid: String): Flow<ProductEntity?>

    @Query("SELECT position FROM products WHERE shopping_uid = :shoppingUid ORDER BY position ASC LIMIT 1")
    fun getFirstPosition(shoppingUid: String): Flow<Int?>

    @Query("SELECT position FROM products WHERE shopping_uid = :shoppingUid ORDER BY position DESC LIMIT 1")
    fun getLastPosition(shoppingUid: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: ProductEntity)

    @Query("UPDATE products SET position = :position, last_modified = :lastModified WHERE product_uid = :productUid")
    fun updatePosition(productUid: String, position: Int, lastModified: Long)

    @Query("UPDATE products SET completed = 1, last_modified = :lastModified WHERE product_uid = :productUid")
    fun completeProduct(productUid: String, lastModified: Long)

    @Query("UPDATE products SET completed = 0, last_modified = :lastModified WHERE product_uid = :productUid")
    fun activeProduct(productUid: String, lastModified: Long)

    @Query("UPDATE products SET pinned = 1, last_modified = :lastModified WHERE product_uid IN (:productUids)")
    fun pinProducts(productUids: List<String>, lastModified: Long)

    @Query("UPDATE products SET pinned = 1, last_modified = :lastModified WHERE product_uid = :productUid")
    fun pinProduct(productUid: String, lastModified: Long)

    @Query("UPDATE products SET pinned = 0, last_modified = :lastModified WHERE product_uid IN (:productUids)")
    fun unpinProducts(productUids: List<String>, lastModified: Long)

    @Query("UPDATE products SET pinned = 0, last_modified = :lastModified WHERE product_uid = :productUid")
    fun unpinProduct(productUid: String, lastModified: Long)

    @Query("DELETE FROM products")
    fun deleteAllProducts()

    @Delete
    fun deleteProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE product_uid IN (:productUids)")
    fun deleteProductsByProductUids(productUids: List<String>)

    @Query("DELETE FROM products WHERE shopping_uid IN (:shoppingUids)")
    fun deleteProductsByShoppingUids(shoppingUids: List<String>)

    @Query("DELETE FROM products WHERE shopping_uid = :shoppingUid")
    fun deleteProductsByShoppingUid(shoppingUid: String)

    @Query("DELETE FROM products WHERE product_uid = :productUid")
    fun deleteProduct(productUid: String)
}