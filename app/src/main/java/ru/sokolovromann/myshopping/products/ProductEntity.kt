package ru.sokolovromann.myshopping.products

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "api_39_products")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id", defaultValue = "")
    val id: String,

    @ColumnInfo(name = "directory", defaultValue = "")
    val directory: String,

    @ColumnInfo(name = "position", defaultValue = "")
    val position: String,

    @ColumnInfo(name = "created", defaultValue = "")
    val created: String,

    @ColumnInfo(name = "last_modified", defaultValue = "")
    val lastModified: String,

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "quantity", defaultValue = "")
    val quantity: String,

    @ColumnInfo(name = "quantity_symbol", defaultValue = "")
    val quantitySymbol: String,

    @ColumnInfo(name = "price", defaultValue = "")
    val price: String,

    @ColumnInfo(name = "cost", defaultValue = "")
    val cost: String,

    @ColumnInfo(name = "discount", defaultValue = "")
    val discount: String,

    @ColumnInfo(name = "discount_type", defaultValue = "")
    val discountType: String,

    @ColumnInfo(name = "taxRate", defaultValue = "")
    val taxRate: String,

    @ColumnInfo(name = "total", defaultValue = "")
    val total: String,

    @ColumnInfo(name = "note", defaultValue = "")
    val note: String,

    @ColumnInfo(name = "manufacturer", defaultValue = "")
    val manufacturer: String,

    @ColumnInfo(name = "brand", defaultValue = "")
    val brand: String,

    @ColumnInfo(name = "size", defaultValue = "")
    val size: String,

    @ColumnInfo(name = "color", defaultValue = "")
    val color: String,

    @ColumnInfo(name = "image", defaultValue = "")
    val image: String,

    @ColumnInfo(name = "status", defaultValue = "")
    val status: String,

    @ColumnInfo(name = "priority", defaultValue = "")
    val priority: String
)