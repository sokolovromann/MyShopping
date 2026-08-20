package ru.sokolovromann.myshopping.core.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Main : Screen

    @Serializable
    data object Purchases : Screen

    @Serializable
    data object Archive : Screen

    @Serializable
    data object Trash : Screen

    @Serializable
    data class AddEditCartDescription(val directory: String) : Screen

    @Serializable
    data class AddEditCartReminder(val directory: String) : Screen

    @Serializable
    data class AddEditCartMoney(val directory: String) : Screen

    @Serializable
    data class CalculateCartTotalChange(val directory: String) : Screen

    @Serializable
    data class Products(val directory: String) : Screen

    @Serializable
    data class AddEditProduct(
        val cartDirectory: String,
        val productDirectory: String? = null
    ) : Screen

    @Serializable
    data class CopyMoveProducts(
        val productDirectories: List<String>,
        val isCopy: Boolean
    ) : Screen

    @Serializable
    data object ProductsWidget : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Dictionary : Screen

    @Serializable
    data object About : Screen

    @Serializable
    data class Migration(val api: Long) : Screen
}