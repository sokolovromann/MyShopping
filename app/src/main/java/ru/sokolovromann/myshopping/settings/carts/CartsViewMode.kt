package ru.sokolovromann.myshopping.settings.carts

sealed class CartsViewMode(val params: Params) {

    enum class Params {

        ProductVertically,

        ProductHorizontally,

        HideProducts;
    }

    data class List(val listParams: Params) : CartsViewMode(listParams)

    data class Grid(val gridParams: Params) : CartsViewMode(gridParams)

    companion object {

        fun classOfOrNull(name: String?, params: Params): CartsViewMode? {
            return when (name) {
                "List" -> List(params)
                "Grid" -> Grid(params)
                else -> null
            }
        }

        fun classOfOrDefault(name: String?, params: Params, defaultValue: CartsViewMode): CartsViewMode {
            return classOfOrNull(name, params) ?: defaultValue
        }
    }

    fun getName(): String {
        return when (this) {
            is List -> "List"
            is Grid -> "Grid"
        }
    }
}