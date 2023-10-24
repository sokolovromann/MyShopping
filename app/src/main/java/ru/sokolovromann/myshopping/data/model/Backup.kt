package ru.sokolovromann.myshopping.data.model

data class Backup(
    val shoppings: List<Shopping> = listOf(),
    val products: List<Product> = listOf(),
    val autocompletes: List<Autocomplete> = listOf(),
    val appConfig: AppConfig = AppConfig(),
    val fileName: String = ""
)