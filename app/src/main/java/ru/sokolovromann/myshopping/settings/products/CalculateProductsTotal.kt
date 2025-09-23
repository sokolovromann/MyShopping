package ru.sokolovromann.myshopping.settings.products

sealed class CalculateProductsTotal(val params: Params?) {

    enum class Params {

        Short,

        Long;
    }

    data class AllProducts(val allParams: Params) : CalculateProductsTotal(allParams)

    data class CompletedProducts(val completedParams: Params) : CalculateProductsTotal(completedParams)

    data class ActiveProducts(val activeParams: Params) : CalculateProductsTotal(activeParams)

    data object DoNotCalculate : CalculateProductsTotal(null)

    companion object {

        fun classOfOrNull(name: String?, params: Params): CalculateProductsTotal? {
            return when (name) {
                "AllProducts" -> AllProducts(params)
                "CompletedProducts" -> CompletedProducts(params)
                "ActiveProducts" -> ActiveProducts(params)
                "DoNotCalculate" -> DoNotCalculate
                else -> null
            }
        }

        fun classOfOrDefault(
            name: String?,
            params: Params, defaultValue: CalculateProductsTotal
        ): CalculateProductsTotal {
            return classOfOrNull(name, params) ?: defaultValue
        }
    }

    fun getName(): String {
        return when (this) {
            is AllProducts -> "AllProducts"
            is CompletedProducts -> "CompletedProducts"
            is ActiveProducts -> "ActiveProducts"
            is DoNotCalculate -> "DoNotCalculate"
        }
    }
}