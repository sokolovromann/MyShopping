package ru.sokolovromann.myshopping.settings.autocompletes

sealed class AutocompletesViewMode(val params: Params) {

    enum class Params {

        NameField,

        BasicFields,

        AllFields;
    }

    data class List(val listParams: Params) : AutocompletesViewMode(listParams)

    data class Grid(val gridParams: Params) : AutocompletesViewMode(gridParams)

    companion object {

        fun classOfOrNull(name: String?, params: Params): AutocompletesViewMode? {
            return when (name) {
                "List" -> List(params)
                "Grid" -> Grid(params)
                else -> null
            }
        }

        fun classOfOrDefault(
            name: String?,
            params: Params,
            defaultValue: AutocompletesViewMode
        ): AutocompletesViewMode {
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