package ru.sokolovromann.myshopping.old

import androidx.room.Embedded
import androidx.room.Relation

data class OldShoppingListEntity(
    @Embedded
    val oldShoppingEntity: OldShoppingEntity = OldShoppingEntity(),

    @Relation(
        parentColumn = "uid",
        entityColumn = "shopping_uid"
    )
    val productEntities: List<OldProductEntity> = listOf()
)