package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.asSearchQuery
import ru.sokolovromann.myshopping.data.utils.sortedAutocompletes
import ru.sokolovromann.myshopping.data.utils.uppercaseFirst

data class AutocompletesWithConfig(
    val autocompletes: List<Autocomplete> = listOf(),
    val appConfig: AppConfig = AppConfig()
) {

    fun groupAutocompletesByName(): Map<String, List<Autocomplete>> {
        return autocompletes
            .sortedAutocompletes()
            .groupBy { it.name.asSearchQuery() }
            .mapKeys { it.key.uppercaseFirst() }
    }

    fun getNames(): List<String> {
        return groupAutocompletesByName().keys.toList()
    }

    fun getUidsByNames(names: List<String>): List<String> {
        val namesAsSearch = names.map { it.asSearchQuery() }
        return autocompletes
            .filter { namesAsSearch.contains(it.name.asSearchQuery()) }
            .map { it.uid }
    }

    fun isEmpty(): Boolean {
        return autocompletes.isEmpty()
    }
}