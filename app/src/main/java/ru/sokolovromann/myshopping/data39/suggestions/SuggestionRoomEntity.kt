package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "api39_suggestions")
data class SuggestionRoomEntity(
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