package ru.sokolovromann.myshopping.data39.carts

import androidx.room.Embedded
import androidx.room.Relation
import ru.sokolovromann.myshopping.data39.products.ProductRoomEntity

data class CartWithProductsRoomEntity(
    @Embedded
    val cart: CartRoomEntity,

    @Relation(parentColumn = "uid", entityColumn = "directory")
    val products: List<ProductRoomEntity>
)