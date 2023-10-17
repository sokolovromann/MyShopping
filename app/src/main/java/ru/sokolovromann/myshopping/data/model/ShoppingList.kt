package ru.sokolovromann.myshopping.data.model

data class ShoppingList(
    val shopping: Shopping = Shopping(),
    val products: List<Product> = listOf()
) {

    fun isCompleted(): Boolean {
        return if (products.isEmpty()) {
            false
        } else {
            products.find { !it.completed } == null
        }
    }
}