package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.exception.InvalidValueException
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ProductWithConfig
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.mapper.AppConfigMapper
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.IdDefaults
import ru.sokolovromann.myshopping.data.model.ShoppingPeriod
import ru.sokolovromann.myshopping.data.utils.sortedProducts
import ru.sokolovromann.myshopping.data.utils.sortedShoppingLists
import javax.inject.Inject

class ShoppingListsRepository @Inject constructor(localDatasource: LocalDatasource) {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getPurchasesWithConfig(): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        return@withContext getShoppingListsWithConfig(ShoppingLocation.PURCHASES)
    }

    suspend fun getArchiveWithConfig(
        period: ShoppingPeriod? = null
    ): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        return@withContext getShoppingListsWithConfig(ShoppingLocation.ARCHIVE, period)
    }

    suspend fun getTrashWithConfig(): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        return@withContext getShoppingListsWithConfig(ShoppingLocation.TRASH)
    }

    suspend fun getRemindersWithConfig(): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getReminders().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { shoppingListEntities, appConfigEntity ->
                ShoppingListsMapper.toShoppingListsWithConfig(
                    entities = shoppingListEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    suspend fun getShortcuts(limit: Int): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getShortcuts(limit).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { shoppingListEntities, appConfigEntity ->
                ShoppingListsMapper.toShoppingListsWithConfig(
                    entities = shoppingListEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    suspend fun getShoppingListWithConfig(
        shoppingUid: String?
    ): Flow<ShoppingListWithConfig> = withContext(dispatcher) {
        return@withContext if (shoppingUid == null) {
            appConfigDao.getAppConfig().map { appConfigEntity ->
                ShoppingListsMapper.toShoppingListWithConfig(
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        } else {
            shoppingListsDao.getShoppingList(shoppingUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { shoppingListEntity, appConfigEntity ->
                    ShoppingListsMapper.toShoppingListWithConfig(
                        entity = shoppingListEntity,
                        appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                    )
                }
            )
        }
    }

    suspend fun getProducts(productUids: List<String>): Flow<List<Product>> = withContext(dispatcher) {
        return@withContext productsDao.getProducts(productUids).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { productEntities, appConfigEntity ->
                ShoppingListsMapper.toProducts(
                    entities = productEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    suspend fun getProductWithConfig(productUid: String?): Flow<ProductWithConfig> = withContext(dispatcher) {
        return@withContext if (productUid == null) {
            appConfigDao.getAppConfig().map { appConfigEntity ->
                ShoppingListsMapper.toProductWithConfig(
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        } else {
            productsDao.getProduct(productUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { productEntity, appConfigEntity ->
                    ShoppingListsMapper.toProductWithConfig(
                        entity = productEntity,
                        appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                    )
                }
            )
        }
    }

    suspend fun saveShoppingLists(shoppingLists: List<ShoppingList>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingLists.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            val shoppingEntities = ShoppingListsMapper.toShoppingEntities(shoppingLists)
            shoppingListsDao.insertShoppings(shoppingEntities)

            val productEntities = ShoppingListsMapper.toProductEntitiesFromShoppingLists(shoppingLists)
            productsDao.insertProducts(productEntities)

            Result.success(Unit)
        }
    }

    suspend fun addShopping(): Result<String> = withContext(dispatcher) {
        val position = nextPositionOrFirst(shoppingListsDao.getLastPosition().firstOrNull())
        val shopping = Shopping(position = position)

        val shoppingList = ShoppingList(shopping = shopping)
        return@withContext saveShoppingList(shoppingList).map { shopping.uid }
    }

    suspend fun moveShoppingListUp(
        shoppingUid: String,
        location: ShoppingLocation = ShoppingLocation.PURCHASES
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingLists = when (location) {
            ShoppingLocation.PURCHASES -> getPurchasesWithConfig()
            ShoppingLocation.ARCHIVE -> getArchiveWithConfig()
            ShoppingLocation.TRASH -> getTrashWithConfig()
        }.firstOrNull()?.getSortedShoppingLists()

        return@withContext if (shoppingLists == null || shoppingLists.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in shoppingLists.indices) {
                val shopping = shoppingLists[index].shopping
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (shopping.uid == shoppingUid) {
                    break
                }
            }

            shoppingListsDao.updatePosition(
                uid = shoppingLists[currentIndex].shopping.uid,
                position = shoppingLists[previousIndex].shopping.position
            )

            shoppingListsDao.updatePosition(
                uid = shoppingLists[previousIndex].shopping.uid,
                position = shoppingLists[currentIndex].shopping.position
            )

            Result.success(Unit)
        }
    }

    suspend fun moveShoppingListDown(
        shoppingUid: String,
        location: ShoppingLocation = ShoppingLocation.PURCHASES
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingLists = when (location) {
            ShoppingLocation.PURCHASES -> getPurchasesWithConfig()
            ShoppingLocation.ARCHIVE -> getArchiveWithConfig()
            ShoppingLocation.TRASH -> getTrashWithConfig()
        }.firstOrNull()?.getSortedShoppingLists()

        return@withContext if (shoppingLists == null || shoppingLists.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in shoppingLists.indices) {
                val shopping = shoppingLists[index].shopping

                currentIndex = index
                if (index < shoppingLists.lastIndex) {
                    nextIndex = index + 1
                }

                if (shopping.uid == shoppingUid) {
                    break
                }
            }

            shoppingListsDao.updatePosition(
                uid = shoppingLists[currentIndex].shopping.uid,
                position = shoppingLists[nextIndex].shopping.position
            )

            shoppingListsDao.updatePosition(
                uid = shoppingLists[nextIndex].shopping.uid,
                position = shoppingLists[currentIndex].shopping.position
            )

            Result.success(Unit)
        }
    }

    suspend fun saveShoppingListName(
        shoppingUid: String,
        name: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.updateName(shoppingUid, name.trim(), lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun saveReminder(
        shoppingUid: String,
        reminder: DateTime,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.updateReminder(shoppingUid, reminder.millis, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun saveShoppingListTotal(
        shoppingUid: String,
        total: Money,
        discount: Money,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.updateTotal(shoppingUid, total.value, discount.value, discount.asPercent, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun saveShoppingListBudget(
        shoppingUid: String,
        budget: Money,
        budgetProducts: DisplayTotal,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.updateBudget(shoppingUid, budget.value, budgetProducts.name, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun saveProduct(product: Product): Result<Unit> = withContext(dispatcher) {
        return@withContext if (product.name.isEmpty()) {
            val exception = InvalidNameException("Name must not be null or empty")
            Result.failure(exception)
        } else {
            val productUid = product.productUid.trim()
            if (productUid.isEmpty()) {
                val exception = InvalidUidException("Uid must not be empty")
                Result.failure(exception)
            } else {
                val position = if (product.id == IdDefaults.NO_ID) {
                    nextPositionOrFirst(productsDao.getLastPosition(product.shoppingUid).firstOrNull())
                } else {
                    product.position
                }
                val newProduct = product.copy(position = position)
                val productEntity = ShoppingListsMapper.toProductEntity(newProduct)
                productsDao.insertProduct(productEntity)

                shoppingListsDao.updateLastModified(productEntity.shoppingUid, productEntity.lastModified)
                Result.success(Unit)
            }
        }
    }

    suspend fun moveProductUp(
        shoppingUid: String,
        productUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingListWithConfig = getShoppingListWithConfig(shoppingUid).firstOrNull()
        val products = shoppingListWithConfig?.getSortedProducts()

        return@withContext if (products == null || products.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in products.indices) {
                val product = products[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (product.productUid == productUid) {
                    break
                }
            }

            productsDao.updatePosition(
                productUid = products[currentIndex].productUid,
                position = products[previousIndex].position
            )

            productsDao.updatePosition(
                productUid = products[previousIndex].productUid,
                position = products[currentIndex].position
            )

            shoppingListsDao.updateLastModified(shoppingUid, lastModified.millis)

            Result.success(Unit)
        }
    }

    suspend fun moveProductDown(
        shoppingUid: String,
        productUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingListWithConfig = getShoppingListWithConfig(shoppingUid).firstOrNull()
        val products = shoppingListWithConfig?.getSortedProducts()

        return@withContext if (products == null || products.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in products.indices) {
                val product = products[index]

                currentIndex = index
                if (index < products.lastIndex) {
                    nextIndex = index + 1
                }

                if (product.productUid == productUid) {
                    break
                }
            }

            productsDao.updatePosition(
                productUid = products[currentIndex].productUid,
                position = products[nextIndex].position
            )

            productsDao.updatePosition(
                productUid = products[nextIndex].productUid,
                position = products[currentIndex].position
            )

            shoppingListsDao.updateLastModified(shoppingUid, lastModified.millis)

            Result.success(Unit)
        }
    }

    suspend fun completeProduct(
        productUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        productsDao.completeProduct(productUid)

        productsDao.getProduct(productUid).first()?.shoppingUid?.let {
            shoppingListsDao.updateLastModified(it, lastModified.millis)
        }

        return@withContext Result.success(Unit)
    }

    suspend fun activeProduct(
        productUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        productsDao.activeProduct(productUid)

        productsDao.getProduct(productUid).first()?.shoppingUid?.let {
            shoppingListsDao.updateLastModified(it, lastModified.millis)
        }

        return@withContext Result.success(Unit)
    }

    suspend fun pinShoppingLists(shoppingUids: List<String>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.pinShoppings(shoppingUids)
            Result.success(Unit)
        }
    }

    suspend fun unpinShoppingLists(shoppingUids: List<String>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.unpinShoppings(shoppingUids)
            Result.success(Unit)
        }
    }

    suspend fun pinProducts(
        productsUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (productsUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            productsDao.pinProducts(productsUids)

            productsUids.forEach { productUid ->
                productsDao.getProduct(productUid).first()?.shoppingUid?.let {
                    shoppingListsDao.updateLastModified(it, lastModified.millis)
                }
            }

            Result.success(Unit)
        }
    }

    suspend fun unpinProducts(
        productsUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (productsUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            productsDao.unpinProducts(productsUids)

            productsUids.forEach { productUid ->
                productsDao.getProduct(productUid).first()?.shoppingUid?.let {
                    shoppingListsDao.updateLastModified(it, lastModified.millis)
                }
            }

            Result.success(Unit)
        }
    }

    suspend fun moveShoppingListsToPurchases(
        shoppingUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.moveToPurchases(shoppingUids, lastModified.millis)
            Result.success(Unit)
        }
    }

    suspend fun moveShoppingListToPurchases(
        shoppingUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.moveToPurchases(shoppingUid, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun moveShoppingListsToArchive(
        shoppingUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.moveToArchive(shoppingUids, lastModified.millis)
            Result.success(Unit)
        }
    }

    suspend fun moveShoppingListToArchive(
        shoppingUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.moveToArchive(shoppingUid, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun moveShoppingListsToTrash(
        shoppingUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidUidException("Uids must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.moveToTrash(shoppingUids, lastModified.millis)
            Result.success(Unit)
        }
    }

    suspend fun moveShoppingListToTrash(
        shoppingUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.moveToTrash(shoppingUid, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun copyShoppingLists(shoppingUids: List<String>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            shoppingUids.forEach { copyShoppingList(it) }
            Result.success(Unit)
        }
    }

    suspend fun copyShoppingList(shoppingUid: String): Result<Unit> = withContext(dispatcher) {
        val shoppingList = shoppingListsDao.getShoppingList(shoppingUid).firstOrNull()
        return@withContext if (shoppingList == null) {
            val exception = InvalidUidException()
            Result.failure(exception)
        } else {
            val newShoppingUid = IdDefaults.createUid()
            val lastModified = DateTime.getCurrentDateTime().millis

            val shopping = shoppingList.shoppingEntity.copy(
                id = IdDefaults.NO_ID,
                position = nextPositionOrFirst(shoppingListsDao.getLastPosition().firstOrNull()),
                uid = newShoppingUid,
                lastModified = lastModified
            )
            shoppingListsDao.insertShopping(shopping)

            val products = shoppingList.productEntities.map {
                it.copy(
                    id = IdDefaults.NO_ID,
                    shoppingUid = newShoppingUid,
                    productUid = IdDefaults.createUid(),
                    lastModified = lastModified
                )
            }
            productsDao.insertProducts(products)

            Result.success(Unit)
        }
    }

    suspend fun copyProducts(
        products: List<Product>,
        shoppingUid: String
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (products.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            val lastModified = DateTime.getCurrentDateTime()
            shoppingListsDao.updateLastModified(shoppingUid, lastModified.millis)

            val lastPosition = nextPositionOrFirst(productsDao.getLastPosition(shoppingUid).firstOrNull())
            val newProducts = products.mapIndexed { index, product ->
                val newPosition = lastPosition + index
                product.copy(
                    id = IdDefaults.NO_ID,
                    position = newPosition,
                    shoppingUid = shoppingUid,
                    productUid = IdDefaults.createUid(),
                    lastModified = lastModified
                )
            }

            val productEntities = ShoppingListsMapper.toProductEntities(newProducts)
            productsDao.insertProducts(productEntities)

            Result.success(Unit)
        }
    }

    suspend fun moveProducts(
        products: List<Product>,
        shoppingUid: String
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (products.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            val lastModified = DateTime.getCurrentDateTime()
            shoppingListsDao.updateLastModified(products.first().shoppingUid, lastModified.millis)
            shoppingListsDao.updateLastModified(shoppingUid, lastModified.millis)

            val lastPosition = nextPositionOrFirst(productsDao.getLastPosition(shoppingUid).firstOrNull())
            val newProducts = products.mapIndexed { index, product ->
                val newPosition = lastPosition + index
                product.copy(
                    position = newPosition,
                    shoppingUid = shoppingUid,
                    lastModified = lastModified
                )
            }

            val productEntities = ShoppingListsMapper.toProductEntities(newProducts)
            productsDao.insertProducts(productEntities)

            Result.success(Unit)
        }
    }

    suspend fun sortShoppingLists(
        sort: Sort,
        automaticSort: Boolean
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingListsWithConfig = getShoppingListsWithConfig().firstOrNull()
        val shoppingLists = shoppingListsWithConfig?.getSortedShoppingLists()
        return@withContext if (automaticSort) {
            appConfigDao.enableAutomaticShoppingsSort(
                sortBy = sort.sortBy.name,
                ascending = sort.ascending
            )
            Result.success(Unit)
        } else {
            if (shoppingListsWithConfig == null || shoppingLists.isNullOrEmpty()) {
                val exception = UnsupportedOperationException("Sort empty list is not supported")
                Result.failure(exception)
            } else {
                appConfigDao.disableAutomaticShoppingsSort(
                    sortBy = sort.sortBy.name,
                    ascending = sort.ascending
                )

                val displayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted
                shoppingLists.sortedShoppingLists(sort, displayCompleted)
                    .forEachIndexed { shoppingIndex, shoppingList ->
                        shoppingListsDao.updatePosition(
                            uid = shoppingList.shopping.uid,
                            position = shoppingIndex
                        )
                    }

                Result.success(Unit)
            }
        }
    }

    suspend fun reverseShoppingLists(automaticSort: Boolean): Result<Unit> = withContext(dispatcher) {
        val shoppingListsWithConfig = getShoppingListsWithConfig().firstOrNull()
        return@withContext if (shoppingListsWithConfig == null) {
            val exception = UnsupportedOperationException("ShoppingList or AppConfig is not exists")
            Result.failure(exception)
        } else {
            val shoppingLists = shoppingListsWithConfig.getSortedShoppingLists()
            val shoppingsSort = shoppingListsWithConfig.getUserPreferences().shoppingsSort
            val sort = shoppingsSort.copy(ascending = !shoppingsSort.ascending)

            if (automaticSort) {
                appConfigDao.enableAutomaticShoppingsSort(
                    sortBy = sort.sortBy.name,
                    ascending = sort.ascending
                )
                Result.success(Unit)
            } else {
                if (shoppingLists.isEmpty()) {
                    val exception = UnsupportedOperationException("Sort empty list is not supported")
                    Result.failure(exception)
                } else {
                    appConfigDao.disableAutomaticShoppingsSort(
                        sortBy = sort.sortBy.name,
                        ascending = sort.ascending
                    )

                    shoppingLists.reversed().forEachIndexed { shoppingIndex, shoppingList ->
                        shoppingListsDao.updatePosition(
                            uid = shoppingList.shopping.uid,
                            position = shoppingIndex
                        )
                    }

                    Result.success(Unit)
                }
            }
        }
    }

    suspend fun sortProducts(
        shoppingUid: String,
        sort: Sort,
        automaticSort: Boolean,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (automaticSort) {
            shoppingListsDao.enableAutomaticSorting(
                uid = shoppingUid,
                sortBy = sort.sortBy.name,
                sortAscending = sort.ascending,
                lastModified = lastModified.millis
            )
            Result.success(Unit)
        } else {
            val shoppingListWithConfig = getShoppingListWithConfig(shoppingUid).firstOrNull()
            val products = shoppingListWithConfig?.getSortedProducts()
            if (shoppingListWithConfig == null || products.isNullOrEmpty()) {
                val exception = UnsupportedOperationException("Sort empty list is not supported")
                Result.failure(exception)
            } else {
                shoppingListsDao.disableAutomaticSorting(
                    uid = shoppingUid,
                    sortBy = "",
                    sortAscending = sort.ascending,
                    lastModified = lastModified.millis
                )

                val displayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted
                products.sortedProducts(sort, displayCompleted)
                    .forEachIndexed { index, product ->
                        productsDao.updatePosition(
                            productUid = product.productUid,
                            position = index
                        )
                    }

                Result.success(Unit)
            }
        }
    }

    suspend fun reverseProducts(
        shoppingUid: String,
        automaticSort: Boolean,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        val shoppingListWithConfig = getShoppingListWithConfig(shoppingUid).firstOrNull()
        if (shoppingListWithConfig == null) {
            val exception = InvalidUidException("Shopping list is not exists")
            return@withContext Result.failure(exception)
        }

        val shopping = shoppingListWithConfig.getShopping()
        val sort = shopping.sort.copy(ascending = !shopping.sort.ascending)
        val products = shoppingListWithConfig.getSortedProducts()

        return@withContext if (automaticSort) {
            shoppingListsDao.enableAutomaticSorting(
                uid = shoppingUid,
                sortBy = sort.sortBy.name,
                sortAscending = sort.ascending,
                lastModified = lastModified.millis
            )
            Result.success(Unit)
        } else {
            if (products.isEmpty()) {
                val exception = UnsupportedOperationException("Sort empty list is not supported")
                Result.failure(exception)
            } else {
                if (shopping.sortFormatted) {
                    shoppingListsDao.disableAutomaticSorting(
                        uid = shoppingUid,
                        sortBy = sort.sortBy.name,
                        sortAscending = sort.ascending,
                        lastModified = lastModified.millis
                    )
                }

                val displayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted
                products.sortedProducts(sort, displayCompleted)
                    .forEachIndexed { index, product ->
                        productsDao.updatePosition(
                            productUid = product.productUid,
                            position = index
                        )
                    }

                Result.success(Unit)
            }
        }
    }

    suspend fun deleteShoppingLists(shoppingUids: List<String>): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.deleteShoppingsByUids(shoppingUids)
            productsDao.deleteProductsByShoppingUids(shoppingUids)
            Result.success(Unit)
        }
    }

    suspend fun deleteShoppingListsBeforeDateTime(dateTime: DateTime): Result<Unit> = withContext(dispatcher) {
        val shoppingListEntities = shoppingListsDao.getTrash().firstOrNull()
        if (shoppingListEntities == null) {
            val exception = InvalidValueException("List must not be null")
            return@withContext Result.failure(exception)
        }

        val shoppingUids = shoppingListEntities
            .filter { it.shoppingEntity.lastModified <= dateTime.millis }
            .map { it.shoppingEntity.uid }

        return@withContext deleteShoppingLists(shoppingUids)
    }

    suspend fun deleteProductsByProductUids(
        shoppingUid: String,
        productsUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (productsUids.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.updateLastModified(shoppingUid, lastModified.millis)
            productsDao.deleteProductsByProductUids(productsUids)
            Result.success(Unit)
        }
    }

    suspend fun deleteReminders(
        shoppingUids: List<String>,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext if (shoppingUids.isEmpty()) {
            val exception = InvalidValueException("List must not be empty")
            Result.failure(exception)
        } else {
            shoppingListsDao.deleteReminders(shoppingUids, lastModified.millis)
            Result.success(Unit)
        }
    }

    suspend fun deleteReminder(
        shoppingUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.deleteReminder(shoppingUid, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    suspend fun deleteShoppingListTotal(
        shoppingUid: String,
        lastModified: DateTime = DateTime.getCurrentDateTime()
    ): Result<Unit> = withContext(dispatcher) {
        shoppingListsDao.deleteTotal(shoppingUid, lastModified.millis)
        return@withContext Result.success(Unit)
    }

    private suspend fun saveShoppingList(shoppingList: ShoppingList): Result<Unit> = withContext(dispatcher) {
        val shoppingEntity = ShoppingListsMapper.toShoppingEntity(shoppingList.shopping)
        shoppingListsDao.insertShopping(shoppingEntity)

        if (shoppingList.products.isNotEmpty()) {
            val productEntities = ShoppingListsMapper.toProductEntities(shoppingList.products)
            productsDao.insertProducts(productEntities)
        }

        return@withContext Result.success(Unit)
    }

    private suspend fun getShoppingListsWithConfig(
        location: ShoppingLocation? = null,
        period: ShoppingPeriod? = null
    ): Flow<ShoppingListsWithConfig> = withContext(dispatcher) {
        val shoppingListsFlow = when (location) {
            ShoppingLocation.PURCHASES -> shoppingListsDao.getPurchases()
            ShoppingLocation.ARCHIVE -> {
                if (period == null || period == ShoppingPeriod.ALL_TIME) {
                    shoppingListsDao.getArchive()
                } else {
                    val minLastModified = ShoppingListsMapper.toMinLastModified(period) ?: 0L
                    shoppingListsDao.getArchive(minLastModified)
                }
            }
            ShoppingLocation.TRASH -> shoppingListsDao.getTrash()
            null -> shoppingListsDao.getAllShoppingLists()
        }

        return@withContext shoppingListsFlow.combine(
            flow = appConfigDao.getAppConfig(),
            transform = { shoppingListEntities, appConfigEntity ->
                ShoppingListsMapper.toShoppingListsWithConfig(
                    entities = shoppingListEntities,
                    appConfig = AppConfigMapper.toAppConfig(appConfigEntity)
                )
            }
        )
    }

    private fun nextPositionOrFirst(lastPosition: Int?): Int {
        return lastPosition?.plus(1) ?: IdDefaults.FIRST_POSITION
    }
}