package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.SortBy

private val defaultAutocompleteSort: Sort = Sort(
    sortBy = SortBy.NAME,
    ascending = true
)

fun List<Autocomplete>.sortedAutocompletes(
    sort: Sort = defaultAutocompleteSort
): List<Autocomplete> {
    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedBy { it.position }
            SortBy.CREATED -> sortedBy { it.id }
            SortBy.LAST_MODIFIED -> sortedBy { it.lastModified.millis }
            SortBy.NAME -> sortedBy { it.name }
            else -> sortedBy { it.id }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedByDescending { it.position }
            SortBy.CREATED -> sortedByDescending { it.id }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified.millis }
            SortBy.NAME -> sortedByDescending { it.name }
            else -> sortedByDescending { it.id }
        }
    }
}