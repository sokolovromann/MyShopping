package ru.sokolovromann.myshopping.core.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CartWithProductsEntity(
    @Embedded
    val cart: CartEntity,

    @Relation(parentColumn = "uid", entityColumn = "directory")
    val products: List<ProductEntity>
)