package ru.sokolovromann.myshopping.core.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SuggestionWithFabricsEntity(
    @Embedded
    val suggestion: SuggestionEntity,

    @Relation(parentColumn = "uid", entityColumn = "directory")
    val fabrics: List<FabricEntity>
)