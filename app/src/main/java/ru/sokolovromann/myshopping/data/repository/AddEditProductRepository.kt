package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.AddEditProduct
import ru.sokolovromann.myshopping.data.repository.model.AddEditProductAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Product

interface AddEditProductRepository {

    suspend fun getAddEditProduct(uid: String): Flow<AddEditProduct?>

    suspend fun getAutocompletes(search: String): Flow<AddEditProductAutocomplete>

    suspend fun addProduct(product: Product)

    suspend fun editProduct(product: Product)

    suspend fun addAutocomplete(autocomplete: Autocomplete)

    suspend fun invertProductsLockQuantity()
}