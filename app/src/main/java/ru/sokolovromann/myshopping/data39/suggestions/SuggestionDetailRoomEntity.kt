package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "api39_suggestion_details")
data class SuggestionDetailRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid", defaultValue = "")
    val uid: String,

    @ColumnInfo(name = "directory", defaultValue = "")
    val directory: String,

    @ColumnInfo(name = "created", defaultValue = "")
    val created: String,

    @ColumnInfo(name = "last_modified", defaultValue = "")
    val lastModified: String,

    @ColumnInfo(name = "type", defaultValue = "")
    val type: String,

    @ColumnInfo(name = "value", defaultValue = "")
    val value: String,

    @ColumnInfo(name = "value_params", defaultValue = "")
    val valueParams: String,

    @ColumnInfo(name = "used", defaultValue = "")
    val used: String
)