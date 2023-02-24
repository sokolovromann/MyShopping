package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.*

interface AddEditProductRepository {

    suspend fun getProducts(search: String): Flow<AddEditProductProducts>

    suspend fun getAddEditProduct(shoppingUid: String, productUid: String?): Flow<AddEditProduct>

    suspend fun getAutocompletes(search: String): Flow<AddEditProductAutocompletes>

    suspend fun addProduct(product: Product)

    suspend fun editProduct(product: Product)

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun saveProductLock(productLock: ProductLock)
}