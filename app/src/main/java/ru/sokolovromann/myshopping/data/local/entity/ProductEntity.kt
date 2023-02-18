package ru.sokolovromann.myshopping.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "position")
    val position: Int = 0,

    @ColumnInfo(name = "product_uid")
    val productUid: String = "",

    @ColumnInfo(name = "shopping_uid")
    val shoppingUid: String = "",

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
    val taxRateAsPercent: Boolean = false,

    @ColumnInfo(name = "note")
    val note: String = "",

    @ColumnInfo(name = "completed")
    val completed: Boolean = false,

    @ColumnInfo(name = "display")
    val display: Boolean = true
)