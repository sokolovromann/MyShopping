package ru.sokolovromann.myshopping.data39.old

import androidx.room.Embedded
import androidx.room.Relation

data class Api15ShoppingListEntity(
    @Embedded
    val shoppingEntity: Api15ShoppingEntity = Api15ShoppingEntity(),

    @Relation(
        parentColumn = "uid",
        entityColumn = "shopping_uid"
    )
    val productEntities: List<Api15ProductEntity> = listOf()
)