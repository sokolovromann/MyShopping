package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.data.local.entity.AppConfigEntity
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.BackupFileEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Backup
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Shopping

object BackupMapper {

    fun toBackupFileEntity(
        shoppingEntities: List<ShoppingEntity>,
        productEntities: List<ProductEntity>,
        autocompleteEntities: List<AutocompleteEntity>,
        appConfigEntity: AppConfigEntity,
        appCodeVersion: Int
    ): BackupFileEntity {
        return BackupFileEntity(
            shoppingEntities = shoppingEntities,
            productEntities = productEntities,
            autocompleteEntities = autocompleteEntities,
            appConfigEntity = appConfigEntity,
            appVersion = appCodeVersion
        )
    }

    fun toBackup(
        shoppings: List<Shopping>,
        products: List<Product>,
        autocompletes: List<Autocomplete>,
        appConfig: AppConfig,
        fileName: String
    ): Backup {
        return Backup(
            shoppings = shoppings,
            products = products,
            autocompletes = autocompletes,
            appConfig = appConfig,
            fileName = fileName
        )
    }
}