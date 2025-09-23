package ru.sokolovromann.myshopping.settings.products

sealed class SortProducts(val params: Params?) {

    enum class Params {

        ByAscending,

        ByDescending;
    }

    data class Created(val byCreatedParams: Params) : SortProducts(byCreatedParams)

    data class LastModified(val byLastModifiedParams: Params) : SortProducts(byLastModifiedParams)

    data class Name(val byNameParams: Params) : SortProducts(byNameParams)

    data class Price(val byPriceParams: Params) : SortProducts(byPriceParams)

    data class Total(val byTotalParams: Params) : SortProducts(byTotalParams)

    data object DoNotSort : SortProducts(null)

    companion object {

        fun classOfOrNull(name: String?, params: Params): SortProducts? {
            return when (name) {
                "Created" -> Created(params)
                "LastModified" -> LastModified(params)
                "Name" -> Name(params)
                "Price" -> Price(params)
                "Total" -> Total(params)
                "DoNotSort" -> DoNotSort
                else -> null
            }
        }

        fun classOfOrDefault(name: String?, params: Params, defaultValue: SortProducts): SortProducts {
            return classOfOrNull(name, params) ?: defaultValue
        }
    }

    fun getName(): String {
        return when (this) {
            is Created -> "Created"
            is LastModified -> "LastModified"
            is Name -> "Name"
            is Price -> "Price"
            is Total -> "Total"
            is DoNotSort -> "DoNotSort"
        }
    }
}