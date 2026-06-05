package ru.sokolovromann.myshopping.core.domain.repository

import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.Product
import ru.sokolovromann.myshopping.core.domain.model.ProductDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID

interface ProductsRepository {

    suspend fun getProduct(uid: UID): Product?

    suspend fun getCurrentProductPosition(): Position?

    suspend fun insertProducts(products: Collection<Product>)

    suspend fun deleteProducts(directory: ProductDirectory)

    suspend fun deleteProducts(uids: Collection<UID>)

    suspend fun clearProducts()
}