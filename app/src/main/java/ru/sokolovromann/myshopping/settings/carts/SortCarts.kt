package ru.sokolovromann.myshopping.settings.carts

sealed class SortCarts(val params: Params?) {

    enum class Params {

        ByAscending,

        ByDescending;
    }

    data class Created(val byCreatedParams: Params) : SortCarts(byCreatedParams)

    data class LastModified(val byLastModifiedParams: Params) : SortCarts(byLastModifiedParams)

    data class Name(val byNameParams: Params) : SortCarts(byNameParams)

    data class Total(val byTotalParams: Params) : SortCarts(byTotalParams)

    data class Reminder(val byReminderParams: Params) : SortCarts(byReminderParams)

    data object DoNotSort : SortCarts(null)

    companion object {

        fun classOfOrNull(name: String?, params: Params): SortCarts? {
            return when (name) {
                "Created" -> Created(params)
                "LastModified" -> LastModified(params)
                "Name" -> Name(params)
                "Total" -> Total(params)
                "Reminder" -> Reminder(params)
                "DoNotSort" -> DoNotSort
                else -> null
            }
        }

        fun classOfOrDefault(name: String?, params: Params, defaultValue: SortCarts): SortCarts {
            return classOfOrNull(name, params) ?: defaultValue
        }
    }

    fun getName(): String {
        return when (this) {
            is Created -> "Created"
            is LastModified -> "LastModified"
            is Name -> "Name"
            is Total -> "Total"
            is Reminder -> "Reminder"
            is DoNotSort -> "DoNotSort"
        }
    }
}