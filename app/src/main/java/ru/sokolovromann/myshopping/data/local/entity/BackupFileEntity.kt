package ru.sokolovromann.myshopping.data.local.entity

data class BackupFileEntity(
    val shoppingEntities: List<ShoppingEntity> = listOf(),
    val productEntities: List<ProductEntity> = listOf(),
    val autocompleteEntities: List<AutocompleteEntity> = listOf(),
    val appVersion: Int = 0
)