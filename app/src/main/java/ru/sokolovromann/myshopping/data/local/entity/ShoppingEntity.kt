package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoppings")
data class ShoppingEntity(
    @PrimaryKey
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

    @ColumnInfo(name = "archived")
    val archived: Boolean = false,

    @ColumnInfo(name = "deleted")
    val deleted: Boolean = false
)