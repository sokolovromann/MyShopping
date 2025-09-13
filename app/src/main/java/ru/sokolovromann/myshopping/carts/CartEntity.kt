package ru.sokolovromann.myshopping.carts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "api_39_carts")
data class CartEntity(
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

    @ColumnInfo(name = "reminder", defaultValue = "")
    val reminder: String,

    @ColumnInfo(name = "repeat_reminder", defaultValue = "")
    val repeatReminder: String,

    @ColumnInfo(name = "discount", defaultValue = "")
    val discount: String,

    @ColumnInfo(name = "discount_type", defaultValue = "")
    val discountType: String,

    @ColumnInfo(name = "filter_discount_by_status", defaultValue = "")
    val filterDiscountByStatus: String,

    @ColumnInfo(name = "total", defaultValue = "")
    val total: String,

    @ColumnInfo(name = "filter_total_by_status", defaultValue = "")
    val filterTotalByStatus: String,

    @ColumnInfo(name = "budget", defaultValue = "")
    val budget: String,

    @ColumnInfo(name = "filter_budget_by_status", defaultValue = "")
    val filterBudgetByStatus: String,

    @ColumnInfo(name = "note", defaultValue = "")
    val note: String,

    @ColumnInfo(name = "image", defaultValue = "")
    val image: String,

    @ColumnInfo(name = "priority", defaultValue = "")
    val priority: String,

    @ColumnInfo(name = "sort_products", defaultValue = "")
    val sortProducts: String,

    @ColumnInfo(name = "sort_products_params", defaultValue = "")
    val sortProductsParams: String,

    @ColumnInfo(name = "group_products", defaultValue = "")
    val groupProducts: String
)