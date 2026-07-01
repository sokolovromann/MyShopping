package ru.sokolovromann.myshopping.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid", defaultValue = "")
    val uid: String,

    @ColumnInfo(name = "directory", defaultValue = "")
    val directory: String,

    @ColumnInfo(name = "position", defaultValue = "")
    val position: String,

    @ColumnInfo(name = "created", defaultValue = "")
    val created: String,

    @ColumnInfo(name = "last_modified", defaultValue = "")
    val lastModified: String,

    @ColumnInfo(name = "status", defaultValue = "")
    val status: String,

    @ColumnInfo(name = "priority", defaultValue = "")
    val priority: String,

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "quantity", defaultValue = "")
    val quantity: String,

    @ColumnInfo(name = "quantity_measurement_unit", defaultValue = "")
    val quantityMeasurementUnit: String,

    @ColumnInfo(name = "unit_price", defaultValue = "")
    val unitPrice: String,

    @ColumnInfo(name = "full_price", defaultValue = "")
    val fullPrice: String,

    @ColumnInfo(name = "discount", defaultValue = "")
    val discount: String,

    @ColumnInfo(name = "discount_measurement_unit", defaultValue = "")
    val discountMeasurementUnit: String,

    @ColumnInfo(name = "tax", defaultValue = "")
    val tax: String,

    @ColumnInfo(name = "cost", defaultValue = "")
    val cost: String,

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
    val image: String
)