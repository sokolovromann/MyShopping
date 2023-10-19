package ru.sokolovromann.myshopping.data.model.mapper

import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ProductWithConfig
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.repository.model.AddEditProduct
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.Id
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotifications
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.data.repository.model.Time
import ru.sokolovromann.myshopping.data.model.UserPreferences

object ShoppingListsMapper {

    fun toAddEditProduct(productWithConfig: ProductWithConfig): AddEditProduct {
        return AddEditProduct(
            product = toProduct(productWithConfig.product),
            appConfig = productWithConfig.appConfig
        )
    }

    fun toShoppingListNotifications(shoppingListsWithConfig: ShoppingListsWithConfig): ShoppingListNotifications {
        return ShoppingListNotifications(
            shoppingListsWithConfig.shoppingLists.map {
                toShoppingList(it, shoppingListsWithConfig.appConfig.userPreferences)
            }
        )
    }

    fun toShoppingListNotification(shoppingListWithConfig: ShoppingListWithConfig): ShoppingListNotification {
        return ShoppingListNotification(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toEditShoppingListTotal(shoppingListWithConfig: ShoppingListWithConfig): EditShoppingListTotal {
        return EditShoppingListTotal(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toEditShoppingListName(shoppingListWithConfig: ShoppingListWithConfig): EditShoppingListName {
        return EditShoppingListName(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toEditReminder(shoppingListWithConfig: ShoppingListWithConfig): EditReminder {
        return EditReminder(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toCalculateChange(shoppingListWithConfig: ShoppingListWithConfig): CalculateChange {
        return CalculateChange(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toShoppingLists(shoppingListsWithConfig: ShoppingListsWithConfig): ShoppingLists {
        return ShoppingLists(
            shoppingLists = shoppingListsWithConfig.shoppingLists.map {
                toShoppingList(it, shoppingListsWithConfig.appConfig.userPreferences)
            },
            appConfig = shoppingListsWithConfig.appConfig
        )
    }

    fun toShoppingLists(shoppingLists: List<ru.sokolovromann.myshopping.data.repository.model.ShoppingList>): List<ShoppingList> {
        return shoppingLists.map { toShoppingList(it) }
    }

    fun toShoppingList(
        shoppingList: ShoppingList,
        userPreferences: UserPreferences
    ): ru.sokolovromann.myshopping.data.repository.model.ShoppingList {
        val shopping = shoppingList.shopping
        return ru.sokolovromann.myshopping.data.repository.model.ShoppingList(
            id = shopping.id,
            position = shopping.position,
            uid = shopping.uid,
            created = Time.NO_TIME.millis,
            lastModified = shopping.lastModified.millis,
            name = shopping.name,
            reminder = shopping.reminder?.millis,
            total = shopping.total,
            totalFormatted = shopping.totalFormatted,
            budget = shopping.budget,
            location = shopping.location,
            completed = shoppingList.isCompleted(),
            products = toRepositoryProductList(shoppingList.products),
            currency = userPreferences.currency,
            displayTotal = userPreferences.displayTotal,
            sort = shopping.sort,
            sortFormatted = shopping.sortFormatted,
            pinned = shopping.pinned
        )
    }

    fun toShoppingList(shoppingList: ru.sokolovromann.myshopping.data.repository.model.ShoppingList): ShoppingList {
        val shopping = Shopping(
            id = shoppingList.id,
            position = shoppingList.position,
            uid = shoppingList.uid,
            lastModified = Time(shoppingList.lastModified),
            name = shoppingList.name,
            reminder = if (shoppingList.reminder == null) null else Time(shoppingList.reminder),
            total = shoppingList.total,
            totalFormatted = shoppingList.totalFormatted,
            budget = shoppingList.budget,
            location = shoppingList.location,
            sort = shoppingList.sort,
            sortFormatted = shoppingList.sortFormatted,
            pinned = shoppingList.pinned
        )

        val products = shoppingList.products.map {
            Product(
                id = it.id,
                position = it.position,
                productUid = it.productUid,
                shoppingUid = it.shoppingUid,
                name = it.name,
                quantity = it.quantity,
                price = it.price,
                discount = it.discount,
                taxRate = it.taxRate,
                total = it.total,
                totalFormatted = it.totalFormatted,
                note = it.note,
                manufacturer = it.manufacturer,
                brand = it.brand,
                size = it.size,
                color = it.color,
                provider = it.provider,
                completed = it.completed,
                pinned = it.pinned
            )
        }

        return ShoppingList(shopping, products)
    }

    fun toRepositoryProductList(products: List<Product>): List<ru.sokolovromann.myshopping.data.repository.model.Product> {
        return products.map { toProduct(it) }
    }

    fun toProductList(products: List<ru.sokolovromann.myshopping.data.repository.model.Product>): List<Product> {
        return products.map { toProduct(it) }
    }

    fun toProducts(shoppingListWithConfig: ShoppingListWithConfig): Products {
        return Products(
            shoppingList = toShoppingList(
                shoppingListWithConfig.shoppingList,
                shoppingListWithConfig.appConfig.userPreferences
            ),
            appConfig = shoppingListWithConfig.appConfig
        )
    }

    fun toProduct(product: Product): ru.sokolovromann.myshopping.data.repository.model.Product {
        return ru.sokolovromann.myshopping.data.repository.model.Product(
            id = product.id,
            position = product.position,
            productUid = product.productUid,
            shoppingUid = product.shoppingUid,
            created = Time.NO_TIME.millis,
            lastModified = product.lastModified.millis,
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            taxRate = product.taxRate,
            total = product.total,
            totalFormatted = product.totalFormatted,
            note = product.note,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            completed = product.completed,
            pinned = product.pinned
        )
    }

    fun toProduct(product: ru.sokolovromann.myshopping.data.repository.model.Product): Product {
        return Product(
            id = product.id,
            position = product.position,
            productUid = product.productUid,
            shoppingUid = product.shoppingUid,
            lastModified = Time(product.lastModified),
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            taxRate = product.taxRate,
            total = product.total,
            totalFormatted = product.totalFormatted,
            note = product.note,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            completed = product.completed,
            pinned = product.pinned
        )
    }

    fun toShoppingEntity(shopping: Shopping): ShoppingEntity {
        return ShoppingEntity(
            id = shopping.id,
            position = shopping.position,
            uid = shopping.uid,
            created = Time.NO_TIME.millis,
            lastModified = shopping.lastModified.millis,
            name = shopping.name,
            reminder = (shopping.reminder ?: Time.NO_TIME).millis,
            total = shopping.total.value,
            totalFormatted = shopping.totalFormatted,
            budget = shopping.budget.value,
            archived = shopping.location == ShoppingLocation.ARCHIVE,
            deleted = shopping.location == ShoppingLocation.TRASH,
            sortBy = shopping.sort.sortBy.name,
            sortAscending = shopping.sort.ascending,
            sortFormatted = shopping.sortFormatted,
            pinned = shopping.pinned
        )
    }

    fun toProductEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            position = product.position,
            productUid = product.productUid,
            shoppingUid = product.shoppingUid,
            created = Time.NO_TIME.millis,
            lastModified = product.lastModified.millis,
            name = product.name,
            quantity = product.quantity.value,
            quantitySymbol = product.quantity.symbol,
            price = product.price.value,
            discount = product.discount.value,
            discountAsPercent = product.discount.asPercent,
            taxRate = product.taxRate.value,
            taxRateAsPercent = product.taxRate.asPercent,
            total = product.total.value,
            totalFormatted = product.totalFormatted,
            note = product.note,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            completed = product.completed,
            pinned = product.pinned
        )
    }

    fun toProductEntities(products: List<Product>): List<ProductEntity> {
        return products.map { toProductEntity(it) }
    }

    fun toProducts(entities: List<ProductEntity>, appConfig: AppConfig): List<Product> {
        return entities.map { toProduct(it, appConfig.userPreferences) }
    }

    fun toShoppingEntities(shoppingLists: List<ShoppingList>): List<ShoppingEntity> {
        return shoppingLists.map { toShoppingEntity(it.shopping) }
    }

    fun toProductEntitiesFromShoppingLists(shoppingLists: List<ShoppingList>): List<ProductEntity> {
        val products = mutableListOf<Product>()
        shoppingLists.forEach { products.addAll(it.products) }
        return toProductEntities(products)
    }

    fun toShoppingListEntity(shoppingList: ShoppingList): ShoppingListEntity {
        return ShoppingListEntity(
            shoppingEntity = toShoppingEntity(shoppingList.shopping),
            productEntities = toProductEntities(shoppingList.products)
        )
    }

    fun toProductWithConfig(
        entity: ProductEntity? = null,
        appConfig: AppConfig
    ): ProductWithConfig {
        val product = if (entity == null) {
            Product()
        } else {
            toProduct(entity, appConfig.userPreferences)
        }
        return ProductWithConfig(
            product = product,
            appConfig = appConfig
        )
    }

    fun toShoppingListWithConfig(
        entity: ShoppingListEntity? = null,
        appConfig: AppConfig
    ): ShoppingListWithConfig {
        val shoppingList = if (entity == null) {
            ShoppingList()
        } else {
            toShoppingList(entity, appConfig)
        }
        return ShoppingListWithConfig(
            shoppingList = shoppingList,
            appConfig = appConfig
        )
    }

    fun toShoppingListsWithConfig(
        entities: List<ShoppingListEntity>,
        appConfig: AppConfig
    ): ShoppingListsWithConfig {
        return ShoppingListsWithConfig(
            shoppingLists = toShoppingLists(entities, appConfig),
            appConfig = appConfig
        )
    }

    fun toPositionOrFirst(lastPosition: Int?): Int {
        return lastPosition?.plus(1) ?: Id.FIRST_POSITION
    }

    private fun toShopping(entity: ShoppingEntity, userPreferences: UserPreferences): Shopping {
        return Shopping(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            lastModified = Time(entity.lastModified),
            name = entity.name,
            reminder = if (entity.reminder == 0L) null else Time(entity.reminder),
            total = Money(
                value = entity.total,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            totalFormatted = entity.totalFormatted,
            budget = Money(
                value = entity.budget,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            location = ShoppingLocation.create(entity.archived, entity.deleted),
            sort = Sort(
                sortBy = SortBy.valueOfOrDefault(entity.sortBy),
                ascending = entity.sortAscending
            ),
            sortFormatted = entity.sortFormatted,
            pinned = entity.pinned
        )
    }

    private fun toProduct(entity: ProductEntity, userPreferences: UserPreferences): Product {
        return Product(
            id = entity.id,
            position = entity.position,
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            lastModified = Time(entity.lastModified),
            name = entity.name,
            quantity = Quantity(
                value = entity.quantity,
                symbol = entity.quantitySymbol,
                decimalFormat = userPreferences.quantityDecimalFormat
            ),
            price = Money(
                value = entity.price,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            discount = Money(
                value = entity.discount,
                currency = userPreferences.currency,
                asPercent = entity.discountAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            taxRate = Money(
                value = entity.taxRate,
                currency = userPreferences.currency,
                asPercent = entity.taxRateAsPercent,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            total = Money(
                value = entity.total,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            ),
            totalFormatted = entity.totalFormatted,
            note = entity.note,
            manufacturer = entity.manufacturer,
            brand = entity.brand,
            size = entity.size,
            color = entity.color,
            provider = entity.provider,
            completed = entity.completed,
            pinned = entity.pinned
        )
    }

    private fun toShoppingList(entity: ShoppingListEntity, appConfig: AppConfig): ShoppingList {
        return ShoppingList(
            shopping = toShopping(entity.shoppingEntity, appConfig.userPreferences),
            products = toProducts(entity.productEntities, appConfig)
        )
    }

    private fun toShoppingLists(
        entities: List<ShoppingListEntity>,
        appConfig: AppConfig
    ): List<ShoppingList> {
        return entities.map { toShoppingList(it, appConfig) }
    }
}