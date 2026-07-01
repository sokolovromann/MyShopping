package ru.sokolovromann.myshopping.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestions")
data class SuggestionEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid", defaultValue = "")
    val uid: String,

    @ColumnInfo(name = "directory", defaultValue = "")
    val directory: String,

    @ColumnInfo(name = "created", defaultValue = "")
    val created: String,

    @ColumnInfo(name = "last_modified", defaultValue = "")
    val lastModified: String,

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "used", defaultValue = "")
    val used: String
)