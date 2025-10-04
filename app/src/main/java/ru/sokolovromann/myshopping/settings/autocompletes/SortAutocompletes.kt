package ru.sokolovromann.myshopping.settings.autocompletes

sealed class SortAutocompletes(val params: Params) {

    enum class Params {

        ByAscending,

        ByDescending;
    }

    data class Name(val byReminderParams: Params) : SortAutocompletes(byReminderParams)

    data class Popularity(val byPopularityParams: Params) : SortAutocompletes(byPopularityParams)

    companion object {

        fun classOfOrNull(name: String?, params: Params): SortAutocompletes? {
            return when (name) {
                "Name" -> Name(params)
                "Popularity" -> Popularity(params)
                else -> null
            }
        }

        fun classOfOrDefault(
            name: String?,
            params: Params,
            defaultValue: SortAutocompletes
        ): SortAutocompletes {
            return classOfOrNull(name, params) ?: defaultValue
        }
    }

    fun getName(): String {
        return when (this) {
            is Name -> "Name"
            is Popularity -> "Popularity"
        }
    }
}