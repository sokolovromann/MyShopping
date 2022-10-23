package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autocompletes")
data class AutocompleteEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: String = "",

    @ColumnInfo(name = "created")
    val created: Long = 0L,

    @ColumnInfo(name = "last_modified")
    val lastModified: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "quantity")
    val quantity: Float = 0f,

    @ColumnInfo(name = "quantity_symbol")
    val quantitySymbol: String = "",

    @ColumnInfo(name = "price")
    val price: Float = 0f,

    @ColumnInfo(name = "discount")
    val discount: Float = 0f,

    @ColumnInfo(name = "discount_as_percent")
    val discountAsPercent: Boolean = false,

    @ColumnInfo(name = "tax_rate")
    val taxRate: Float = 0f,

    @ColumnInfo(name = "tax_rate_as_percent")
    val taxRateAsPercent: Boolean = false
)