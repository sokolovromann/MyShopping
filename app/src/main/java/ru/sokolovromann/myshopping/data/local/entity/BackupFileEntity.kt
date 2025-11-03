package ru.sokolovromann.myshopping.data.local.entity

import ru.sokolovromann.myshopping.data39.old.OldAutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.OldProductEntity
import ru.sokolovromann.myshopping.data39.old.OldShoppingEntity

data class BackupFileEntity(
    val shoppingEntities: List<OldShoppingEntity> = listOf(),
    val productEntities: List<OldProductEntity> = listOf(),
    val autocompleteEntities: List<OldAutocompleteEntity> = listOf(),
    val appVersion: Int = 0
)