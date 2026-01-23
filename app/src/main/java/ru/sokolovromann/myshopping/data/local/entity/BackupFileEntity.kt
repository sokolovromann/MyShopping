package ru.sokolovromann.myshopping.data.local.entity

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15ProductEntity
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingEntity

data class BackupFileEntity(
    val shoppingEntities: List<Api15ShoppingEntity> = listOf(),
    val productEntities: List<Api15ProductEntity> = listOf(),
    val autocompleteEntities: List<Api15AutocompleteEntity> = listOf(),
    val appVersion: Int = 0
)