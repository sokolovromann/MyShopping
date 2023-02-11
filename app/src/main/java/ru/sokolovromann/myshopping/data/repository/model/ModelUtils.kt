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

fun List<ShoppingList>.sortShoppingLists(displayCompleted: DisplayCompleted): List<ShoppingList> {
    val partition = this.partition { it.completed }
    val sortedCompleted: List<ShoppingList> = partition.first.sortedBy { it.position }
    val sortedActive: List<ShoppingList> = partition.second.sortedBy { it.position }

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

fun List<Product>.sortProducts(displayCompleted: DisplayCompleted): List<Product> {
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