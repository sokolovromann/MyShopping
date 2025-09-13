package ru.sokolovromann.myshopping.carts

import androidx.room.Embedded
import androidx.room.Relation
import ru.sokolovromann.myshopping.products.ProductEntity

data class CartWithProductsEntity(
    @Embedded
    val cart: CartEntity,

    @Relation(parentColumn = "id", entityColumn = "directory")
    val products: List<ProductEntity>
)