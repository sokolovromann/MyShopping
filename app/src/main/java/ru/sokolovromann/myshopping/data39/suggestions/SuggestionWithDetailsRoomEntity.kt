package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.Embedded
import androidx.room.Relation

data class SuggestionWithDetailsRoomEntity(
    @Embedded
    val suggestion: SuggestionRoomEntity,

    @Relation(parentColumn = "uid", entityColumn = "directory")
    val details: List<SuggestionDetailRoomEntity>
)