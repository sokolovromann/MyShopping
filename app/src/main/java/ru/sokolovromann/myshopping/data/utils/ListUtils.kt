package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Shopping
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
    sort: Sort = defaultShoppingListsSort,
    displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue
): List<ShoppingList> {
    val sortedShoppings = if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> this.sortedBy { it.shopping.position }
            SortBy.CREATED -> this.sortedBy { it.shopping.id }
            SortBy.LAST_MODIFIED -> this.sortedBy { it.shopping.lastModified.millis }
            SortBy.NAME -> this.sortedBy { it.shopping.name }
            SortBy.TOTAL -> this.sortedBy { it.shopping.total.value }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> this.sortedByDescending { it.shopping.position }
            SortBy.CREATED -> this.sortedByDescending { it.shopping.id }
            SortBy.LAST_MODIFIED -> this.sortedByDescending { it.shopping.lastModified.millis }
            SortBy.NAME -> this.sortedByDescending { it.shopping.name }
            SortBy.TOTAL -> this.sortedByDescending { it.shopping.total.value }
        }
    }

    val productsDisplayCompleted = if (displayCompleted == DisplayCompleted.HIDE) {
        DisplayCompleted.NO_SPLIT
    } else {
        displayCompleted
    }
    val sortedShoppingList = sortedShoppings.map {
        val productsSort = if (it.shopping.sortFormatted) it.shopping.sort else sort
        it.copy(products = it.products.sortedProducts(productsSort, productsDisplayCompleted))
    }
    val partition = sortedShoppingList.partition { it.isCompleted() }

    return buildList {
        when (displayCompleted) {
            DisplayCompleted.FIRST -> {
                addAll(partition.first)
                addAll(partition.second)
            }
            DisplayCompleted.LAST -> {
                addAll(partition.second)
                addAll(partition.first)
            }
            DisplayCompleted.HIDE -> {
                addAll(partition.second)
            }
            DisplayCompleted.NO_SPLIT -> {
                addAll(sortedShoppingList)
            }
        }
    }
}

fun List<Product>.sortedProducts(
    sort: Sort = defaultProductsSort,
    displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue
): List<Product> {
    val sorted = if (sort.ascending) {
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

    val partition = sorted.partition { it.completed }
    return buildList {
        when (displayCompleted) {
            DisplayCompleted.FIRST -> {
                addAll(partition.first)
                addAll(partition.second)
            }
            DisplayCompleted.LAST -> {
                addAll(partition.second)
                addAll(partition.first)
            }
            DisplayCompleted.HIDE -> {
                addAll(partition.second)
            }
            DisplayCompleted.NO_SPLIT -> {
                addAll(sorted)
            }
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

fun List<Product>.toProductsString(): String {
    val builder = StringBuilder()
    forEachIndexed { index, product ->
        builder.append(product.name)
        if (index < lastIndex) {
            builder.append(", ")
        }
    }
    return builder.toString()
}

fun List<Shopping>.toShoppingsString(): String {
    val builder = StringBuilder()
    forEachIndexed { index, shopping ->
        builder.append(shopping.name)
        if (index < lastIndex) {
            builder.append(", ")
        }
    }
    return builder.toString()
}

fun List<Autocomplete>.toAutocompletesString(): String {
    val builder = StringBuilder()
    forEachIndexed { index, autocomplete ->
        builder.append(autocomplete.name)
        if (index < lastIndex) {
            builder.append(", ")
        }
    }
    return builder.toString()
}