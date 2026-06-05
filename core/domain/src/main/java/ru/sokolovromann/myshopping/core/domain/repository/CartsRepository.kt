package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.Cart
import ru.sokolovromann.myshopping.core.domain.model.CartDirectory
import ru.sokolovromann.myshopping.core.domain.model.CartWithProducts
import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.UID

interface CartsRepository {

    fun observeCartsWithProducts(directory: CartDirectory): Flow<Collection<CartWithProducts>>

    fun observeCartWithProducts(uid: UID): Flow<CartWithProducts?>

    suspend fun getCart(uid: UID): Cart?

    suspend fun getCurrentCartPosition(): Position?

    suspend fun insertCarts(carts: Collection<Cart>)

    suspend fun deleteCarts(directory: CartDirectory)

    suspend fun deleteCarts(uids: Collection<UID>)

    suspend fun clearCarts()
}