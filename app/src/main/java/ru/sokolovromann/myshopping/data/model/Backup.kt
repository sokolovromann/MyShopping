package ru.sokolovromann.myshopping.data.model

data class Backup(
    val shoppings: List<Shopping> = listOf(),
    val products: List<Product> = listOf(),
    val fileName: String = ""
)