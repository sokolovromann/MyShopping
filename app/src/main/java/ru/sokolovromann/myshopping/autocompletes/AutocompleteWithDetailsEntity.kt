package ru.sokolovromann.myshopping.autocompletes

import androidx.room.Embedded
import androidx.room.Relation

data class AutocompleteWithDetailsEntity(
    @Embedded
    val autocomplete: AutocompleteEntity,

    @Relation(parentColumn = "id", entityColumn = "directory")
    val details: List<AutocompleteDetailsEntity>
)