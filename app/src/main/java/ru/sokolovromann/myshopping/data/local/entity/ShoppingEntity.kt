package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoppings")
data class ShoppingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "position")
    val position: Int = 0,

    @ColumnInfo(name = "uid")
    val uid: String = "",

    @ColumnInfo(name = "created")
    val created: Long = 0L,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "reminder")
    val reminder: Long = 0L,

    @ColumnInfo(name = "total")
    val total: Float = 0f,

    @ColumnInfo(name = "total_formatted")
    val totalFormatted: Boolean = false,

    @ColumnInfo(name = "archived")
    val archived: Boolean = false,

    @ColumnInfo(name = "deleted")
    val deleted: Boolean = false,

    @ColumnInfo(name = "sort_by")
    val sortBy: String = "",

    @ColumnInfo(name = "sort_ascending")
    val sortAscending: Boolean = true,

    @ColumnInfo(name = "sort_formatted")
    val sortFormatted: Boolean = false,

    @ColumnInfo(name = "pinned")
    val pinned: Boolean = false
)