package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy

private val defaultShoppingListsSort: Sort = Sort(
    sortBy = SortBy.POSITION,
    ascending = true
)

private val defaultProductsSort: Sort = Sort(
    sortBy = SortBy.POSITION,
    ascending = true
)

private val defaultAutocompletesSort: Sort = Sort(
    sortBy = SortBy.NAME,
    ascending = true
)

fun List<ShoppingList>.sortedShoppingLists(
    sort: Sort = defaultShoppingListsSort
): List<ShoppingList> {
    val shoppingLists = this.map {
        it.copy(products = it.products.sortedProducts(sort))
    }

    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> shoppingLists.sortedBy { it.shopping.position }
            SortBy.CREATED -> shoppingLists.sortedBy { it.shopping.id }
            SortBy.LAST_MODIFIED -> shoppingLists.sortedBy { it.shopping.lastModified.millis }
            SortBy.NAME -> shoppingLists.sortedBy { it.shopping.name }
            SortBy.TOTAL -> shoppingLists.sortedBy { it.shopping.total.value }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> shoppingLists.sortedByDescending { it.shopping.position }
            SortBy.CREATED -> shoppingLists.sortedByDescending { it.shopping.id }
            SortBy.LAST_MODIFIED -> shoppingLists.sortedByDescending { it.shopping.lastModified.millis }
            SortBy.NAME -> shoppingLists.sortedByDescending { it.shopping.name }
            SortBy.TOTAL -> shoppingLists.sortedByDescending { it.shopping.total.value }
        }
    }
}

fun List<Product>.sortedProducts(
    sort: Sort = defaultProductsSort
): List<Product> {
    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedBy { it.position }
            SortBy.CREATED -> sortedBy { it.id }
            SortBy.LAST_MODIFIED -> sortedBy { it.lastModified.millis }
            SortBy.NAME -> sortedBy { it.name }
            SortBy.TOTAL -> sortedBy { it.total.value }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedByDescending { it.position }
            SortBy.CREATED -> sortedByDescending { it.id }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified.millis }
            SortBy.NAME -> sortedByDescending { it.name }
            SortBy.TOTAL -> sortedByDescending { it.total.value }
        }
    }
}

fun List<Autocomplete>.sortedAutocompletes(
    sort: Sort = defaultAutocompletesSort
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