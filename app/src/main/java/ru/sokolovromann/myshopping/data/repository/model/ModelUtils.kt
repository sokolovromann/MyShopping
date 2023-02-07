package ru.sokolovromann.myshopping.data.repository.model

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

fun List<ShoppingList>.sortShoppingLists(sort: Sort, displayCompleted: DisplayCompleted): List<ShoppingList> {
    val partition = this.partition { it.completed }
    var sortedCompleted: List<ShoppingList> = mutableListOf()
    var sortedActive: List<ShoppingList> = mutableListOf()

    when (sort.sortBy) {
        SortBy.CREATED -> {
            if (sort.ascending) {
                sortedCompleted = partition.first.sortedBy { it.created }
                sortedActive = partition.second.sortedBy { it.created }
            } else {
                sortedCompleted = partition.first.sortedByDescending { it.created }
                sortedActive = partition.second.sortedByDescending { it.created }
            }
        }
        SortBy.LAST_MODIFIED -> {
            if (sort.ascending) {
                sortedCompleted = partition.first.sortedBy { it.lastModified }
                sortedActive = partition.second.sortedBy { it.lastModified }
            } else {
                sortedCompleted = partition.first.sortedByDescending { it.lastModified }
                sortedActive = partition.second.sortedByDescending { it.lastModified }
            }
        }
        SortBy.NAME -> {
            if (sort.ascending) {
                sortedCompleted = partition.first.sortedBy { it.name }
                sortedActive = partition.second.sortedBy { it.name }
            } else {
                sortedCompleted = partition.first.sortedByDescending { it.name }
                sortedActive = partition.second.sortedByDescending { it.name }
            }
        }
        SortBy.TOTAL -> {
            if (sort.ascending) {
                sortedCompleted = partition.first.sortedBy { it.calculateTotal().value }
                sortedActive = partition.second.sortedBy { it.calculateTotal().value }
            } else {
                sortedCompleted = partition.first.sortedByDescending { it.calculateTotal().value }
                sortedActive = partition.second.sortedByDescending { it.calculateTotal().value }
            }
        }
    }

    val allShoppingLists: MutableList<ShoppingList> = mutableListOf()
    when (displayCompleted) {
        DisplayCompleted.FIRST -> {
            allShoppingLists.addAll(sortedCompleted)
            allShoppingLists.addAll(sortedActive)
        }
        DisplayCompleted.LAST -> {
            allShoppingLists.addAll(sortedActive)
            allShoppingLists.addAll(sortedCompleted)
        }
        DisplayCompleted.HIDE -> {
            allShoppingLists.addAll(sortedActive)
        }
    }

    return allShoppingLists.toList()
}

fun List<Product>.sortProducts(sort: Sort, displayCompleted: DisplayCompleted): List<Product> {
    val partition = this.partition { it.completed }
    val sortedCompleted: List<Product> = partition.first.sortedBy { it.position }
    val sortedActive: List<Product> = partition.second.sortedBy { it.position }

    val allProducts: MutableList<Product> = mutableListOf()
    when (displayCompleted) {
        DisplayCompleted.FIRST -> {
            allProducts.addAll(sortedCompleted)
            allProducts.addAll(sortedActive)
        }
        DisplayCompleted.LAST -> {
            allProducts.addAll(sortedActive)
            allProducts.addAll(sortedCompleted)
        }
        DisplayCompleted.HIDE -> {
            allProducts.addAll(sortedActive)
        }
    }

    return allProducts.toList()
}

fun List<Autocomplete>.sortAutocompletes(sort: Sort): List<Autocomplete> {
    return when (sort.sortBy) {
        SortBy.CREATED -> {
            if (sort.ascending) {
                this.sortedBy { it.created }
            } else {
                this.sortedByDescending { it.created }
            }
        }

        SortBy.LAST_MODIFIED -> {
            if (sort.ascending) {
                this.sortedBy { it.lastModified }
            } else {
                this.sortedByDescending { it.lastModified }
            }
        }

        SortBy.NAME -> {
            if (sort.ascending) {
                this.sortedBy { it.name }
            } else {
                this.sortedByDescending { it.name }
            }
        }

        else -> {
            if (sort.ascending) {
                this.sortedBy { it.name }
            } else {
                this.sortedByDescending { it.name }
            }
        }
    }
}