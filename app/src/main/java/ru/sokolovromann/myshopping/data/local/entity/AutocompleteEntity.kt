package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autocompletes")
data class AutocompleteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "uid")
    val uid: String = "",

    @ColumnInfo(name = "created")
    val created: Long = 0L,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = ""
)