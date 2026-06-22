package ru.sokolovromann.myshopping.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api39_carts")
data class CartEntity(
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

    @ColumnInfo(name = "priority", defaultValue = "")
    val priority: String,

    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,

    @ColumnInfo(name = "reminder", defaultValue = "")
    val reminder: String,

    @ColumnInfo(name = "repeat_reminder", defaultValue = "")
    val repeatReminder: String,

    @ColumnInfo(name = "discount", defaultValue = "")
    val discount: String,

    @ColumnInfo(name = "discount_type", defaultValue = "")
    val discountMeasurementUnit: String,

    @ColumnInfo(name = "filter_discount_by_status", defaultValue = "")
    val filterDiscountByProductStatus: String,

    @ColumnInfo(name = "total", defaultValue = "")
    val total: String,

    @ColumnInfo(name = "filter_total_by_status", defaultValue = "")
    val filterTotalByProductStatus: String,

    @ColumnInfo(name = "budget", defaultValue = "")
    val budget: String,

    @ColumnInfo(name = "filter_budget_by_status", defaultValue = "")
    val filterBudgetByProductStatus: String,

    @ColumnInfo(name = "note", defaultValue = "")
    val note: String,

    @ColumnInfo(name = "image", defaultValue = "")
    val image: String,

    @ColumnInfo(name = "sort", defaultValue = "")
    val sortProduct: String,

    @ColumnInfo(name = "sort_params", defaultValue = "")
    val sortProductByAscending: String,

    @ColumnInfo(name = "group", defaultValue = "")
    val groupProductByStatus: String
)