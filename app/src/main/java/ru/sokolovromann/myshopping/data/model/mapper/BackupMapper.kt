package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.old.OldAutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.old.OldProductEntity
import ru.sokolovromann.myshopping.old.OldShoppingEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Backup
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Shopping

object BackupMapper {

    fun toBackupFileEntity(
        shoppingEntities: List<OldShoppingEntity>,
        productEntities: List<OldProductEntity>,
        autocompleteEntities: List<OldAutocompleteEntity>,
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