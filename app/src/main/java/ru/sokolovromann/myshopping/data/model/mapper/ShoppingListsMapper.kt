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
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.IdDefaults
import ru.sokolovromann.myshopping.data.model.UserPreferences

object ShoppingListsMapper {

    fun toShoppingEntity(shopping: Shopping): ShoppingEntity {
        return ShoppingEntity(
            id = shopping.id,
            position = shopping.position,
            uid = shopping.uid,
            created = DateTime.NO_DATE_TIME.millis,
            lastModified = shopping.lastModified.millis,
            name = shopping.name,
            reminder = (shopping.reminder ?: DateTime.NO_DATE_TIME).millis,
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
            created = DateTime.NO_DATE_TIME.millis,
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

    fun toShoppings(
        shoppingEntities: List<ShoppingEntity>,
        productEntities: List<ProductEntity>,
        appConfig: AppConfig
    ): List<Shopping> {
        return shoppingEntities.map { shoppingEntity ->
            val shoppingProductEntities = productEntities.filter { productEntity ->
                productEntity.shoppingUid == shoppingEntity.uid
            }
            val products = toProducts(shoppingProductEntities, appConfig)
            val productsTotal = calculateProductsTotal(
                products = products,
                userPreferences = appConfig.userPreferences
            )
            toShopping(shoppingEntity, productsTotal, appConfig.userPreferences)
        }
    }

    fun toProductEntitiesFromShoppingLists(shoppingLists: List<ShoppingList>): List<ProductEntity> {
        val products = mutableListOf<Product>()
        shoppingLists.forEach { products.addAll(it.products) }
        return toProductEntities(products)
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
        return lastPosition?.plus(1) ?: IdDefaults.FIRST_POSITION
    }

    private fun toShopping(
        entity: ShoppingEntity,
        productsTotal: Money,
        userPreferences: UserPreferences
    ): Shopping {
        val total = if (entity.totalFormatted) {
            Money(
                value = entity.total,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            )
        } else {
            productsTotal
        }

        return Shopping(
            id = entity.id,
            position = entity.position,
            uid = entity.uid,
            lastModified = DateTime(entity.lastModified),
            name = entity.name,
            reminder = if (entity.reminder == 0L) null else DateTime(entity.reminder),
            total = total,
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
        val quantity = Quantity(
            value = entity.quantity,
            symbol = entity.quantitySymbol,
            decimalFormat = userPreferences.quantityDecimalFormat
        )

        val price = Money(
            value = entity.price,
            currency = userPreferences.currency,
            asPercent = false,
            decimalFormat = userPreferences.moneyDecimalFormat
        )

        val discount = Money(
            value = entity.discount,
            currency = userPreferences.currency,
            asPercent = entity.discountAsPercent,
            decimalFormat = userPreferences.moneyDecimalFormat
        )

        val taxRate = userPreferences.taxRate

        val calculateTotal = if (entity.totalFormatted) {
            val total = Money(
                value = entity.total,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            )
            if (total.isEmpty()) price else total
        } else {
            val quantityValue = if (quantity.isEmpty()) 1f else quantity.value
            val total = quantityValue * price.value
            val discountValue = discount.calculateValueFromPercent(total)
            val taxRateValue = taxRate.calculateValueFromPercent(total)

            val totalWithDiscountAndTaxRate = total - discountValue + taxRateValue
            Money(
                value = totalWithDiscountAndTaxRate,
                currency = userPreferences.currency,
                asPercent = false,
                decimalFormat = userPreferences.moneyDecimalFormat
            )
        }

        return Product(
            id = entity.id,
            position = entity.position,
            productUid = entity.productUid,
            shoppingUid = entity.shoppingUid,
            lastModified = DateTime(entity.lastModified),
            name = entity.name,
            quantity = quantity,
            price = price,
            discount = discount,
            taxRate = taxRate,
            total = calculateTotal,
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
        val products = toProducts(entity.productEntities, appConfig)
        val productsTotal = calculateProductsTotal(products, appConfig.userPreferences)
        return ShoppingList(
            shopping = toShopping(entity.shoppingEntity, productsTotal, appConfig.userPreferences),
            products = products
        )
    }

    private fun toShoppingLists(
        entities: List<ShoppingListEntity>,
        appConfig: AppConfig
    ): List<ShoppingList> {
        return entities.map { toShoppingList(it, appConfig) }
    }

    private fun calculateProductsTotal(
        products: List<Product>,
        userPreferences: UserPreferences
    ): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach {
            val totalValue = it.total.value

            all += totalValue
            if (it.completed) {
                completed += totalValue
            } else {
                active += totalValue
            }
        }

        val total = when (userPreferences.displayTotal) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        return Money(
            value = total,
            currency = userPreferences.currency,
            asPercent = false,
            decimalFormat = userPreferences.moneyDecimalFormat
        )
    }
}