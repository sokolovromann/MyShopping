package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ShoppingListEntity(
    @Embedded
    val shoppingEntity: ShoppingEntity = ShoppingEntity(),

    @Relation(
        parentColumn = "uid",
        entityColumn = "shopping_uid"
    )
    val productEntities: List<ProductEntity> = listOf()
)