package ru.sokolovromann.myshopping.data.utils

import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.UserPreferencesDefaults

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
    displayCompleted: DisplayCompleted = DisplayCompleted.DefaultValue,
    displayEmptyShoppings: Boolean = UserPreferencesDefaults.DISPLAY_EMPTY_SHOPPINGS
): List<ShoppingList> {
    val shoppings = if (displayEmptyShoppings) {
        this
    } else {
        this.filter { it.isProductsNotEmpty() }
    }

    val sortedShoppings = if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> shoppings.sortedBy { it.shopping.position }
            SortBy.CREATED -> shoppings.sortedBy { it.shopping.id }
            SortBy.LAST_MODIFIED -> shoppings.sortedBy { it.shopping.lastModified.millis }
            SortBy.NAME -> shoppings.sortedBy { it.shopping.name }
            SortBy.TOTAL -> shoppings.sortedBy { it.shopping.total.value.toFloat() }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> shoppings.sortedByDescending { it.shopping.position }
            SortBy.CREATED -> shoppings.sortedByDescending { it.shopping.id }
            SortBy.LAST_MODIFIED -> shoppings.sortedByDescending { it.shopping.lastModified.millis }
            SortBy.NAME -> shoppings.sortedByDescending { it.shopping.name }
            SortBy.TOTAL -> shoppings.sortedByDescending { it.shopping.total.value.toFloat() }
        }
    }

    val sortedShoppingList = sortedShoppings.map {
        val productsSort = if (it.shopping.sortFormatted) {
            it.shopping.sort
        } else {
            Sort(sortBy = SortBy.POSITION, ascending = true)
        }
        val products = it.products.sortedProducts(
            sort = productsSort,
            displayCompleted = displayCompleted
        )
        if (displayCompleted == DisplayCompleted.HIDE && it.products.size > products.size) {
            val productsWithHide = products.toMutableList().apply {
                add(Product(name = "...", completed = true))
            }
            it.copy(products = productsWithHide.toList())
        } else {
            it.copy(products = products)
        }
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
            SortBy.TOTAL -> sortedBy { it.total.value.toFloat() }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedByDescending { it.position }
            SortBy.CREATED -> sortedByDescending { it.id }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified.millis }
            SortBy.NAME -> sortedByDescending { it.name }
            SortBy.TOTAL -> sortedByDescending { it.total.value.toFloat() }
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