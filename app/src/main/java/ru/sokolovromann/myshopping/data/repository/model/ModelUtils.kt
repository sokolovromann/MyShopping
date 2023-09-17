package ru.sokolovromann.myshopping.data.repository.model

import java.text.DecimalFormat

fun String.formatFirst(uppercase: Boolean): String {
    return if (this.isEmpty() || !uppercase) {
        this
    } else {
        this.replaceFirst(
            oldValue = this.first().toString(),
            newValue = this.first().uppercase()
        )
    }
}

fun String.toSearch(): String {
    return this.lowercase()
}

fun DecimalFormat.formatValueWithoutSeparators(value: Float): String {
    val spaceCode = 32
    val periodCode = 46
    val formatSymbols = decimalFormatSymbols.apply {
        minusSign = Char(spaceCode)
        groupingSeparator = Char(spaceCode)
        decimalSeparator = Char(periodCode)
        naN = ""
    }
    return apply { decimalFormatSymbols = formatSymbols }
        .format(value)
        .replace(" ", "")
}

fun DecimalFormat.isDisplayZeros(): Boolean {
    return minimumFractionDigits > 0
}

fun List<ShoppingList>.sortShoppingLists(sort: Sort = Sort()): List<ShoppingList> {
    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedBy { it.position }
            SortBy.CREATED -> sortedBy { it.created }
            SortBy.LAST_MODIFIED -> sortedBy { it.lastModified }
            SortBy.NAME -> sortedBy { it.name }
            SortBy.TOTAL -> sortedBy { it.calculateTotal().value }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedByDescending { it.position }
            SortBy.CREATED -> sortedByDescending { it.created }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified }
            SortBy.NAME -> sortedByDescending { it.name }
            SortBy.TOTAL -> sortedByDescending { it.calculateTotal().value }
        }
    }
}

fun List<Product>.sortProducts(sort: Sort = Sort()): List<Product> {
    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedBy { it.position }
            SortBy.CREATED -> sortedBy { it.created }
            SortBy.LAST_MODIFIED -> sortedBy { it.lastModified }
            SortBy.NAME -> sortedBy { it.name }
            SortBy.TOTAL -> sortedBy { it.total.value }
        }
    } else {
        when (sort.sortBy) {
            SortBy.POSITION -> sortedByDescending { it.position }
            SortBy.CREATED -> sortedByDescending { it.created }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified }
            SortBy.NAME -> sortedByDescending { it.name }
            SortBy.TOTAL -> sortedByDescending { it.total.value }
        }
    }
}

fun List<Autocomplete>.sortAutocompletes(
    sort: Sort = Sort(sortBy = SortBy.NAME)
): List<Autocomplete> {
    return if (sort.ascending) {
        when (sort.sortBy) {
            SortBy.CREATED -> sortedBy { it.created }
            SortBy.LAST_MODIFIED -> sortedBy { it.lastModified }
            SortBy.NAME -> sortedBy { it.name }
            else -> sortedBy { it.id }
        }
    } else {
        when (sort.sortBy) {
            SortBy.CREATED -> sortedByDescending { it.created }
            SortBy.LAST_MODIFIED -> sortedByDescending { it.lastModified }
            SortBy.NAME -> sortedByDescending { it.name }
            else -> sortedByDescending { it.id }
        }
    }
}

fun List<ShoppingList>.splitShoppingLists(displayCompleted: DisplayCompleted): List<ShoppingList> {
    val partition = partition { it.completed }
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
            DisplayCompleted.NO_SPLIT -> {}
        }
    }
}

fun List<Product>.splitProducts(displayCompleted: DisplayCompleted): List<Product> {
    val partition = this.partition { it.completed }
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
            DisplayCompleted.NO_SPLIT -> {}
        }
    }
}