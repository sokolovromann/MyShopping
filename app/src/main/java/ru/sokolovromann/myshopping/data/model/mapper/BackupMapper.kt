package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data39.old.Api15ProductEntity
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Backup
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Shopping

object BackupMapper {

    fun toBackupFileEntity(
        shoppingEntities: List<Api15ShoppingEntity>,
        productEntities: List<Api15ProductEntity>,
        autocompleteEntities: List<Api15AutocompleteEntity>,
        appCodeVersion: Int
    ): BackupFileEntity {
        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            appVersion = appCodeVersion
        )
    }

    fun toBackup(
        shoppings: List<Shopping>,
        products: List<Product>,
        autocompletes: List<Autocomplete>,
        fileName: String
    ): Backup {
        return Backup(
            shoppings = shoppings,
            products = products,
            autocompletes = autocompletes,
            fileName = fileName
        )
    }
}