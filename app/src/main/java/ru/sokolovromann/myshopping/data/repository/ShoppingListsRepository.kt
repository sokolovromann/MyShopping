package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.model.AddEditProduct
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange
import ru.sokolovromann.myshopping.data.repository.model.EditReminder
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListName
import ru.sokolovromann.myshopping.data.repository.model.EditShoppingListTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotifications
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import javax.inject.Inject

class ShoppingListsRepository @Inject constructor(
    localDatasource: LocalDatasource,
    private val mapping: RepositoryMapping
) {

    private val shoppingListsDao = localDatasource.getShoppingListsDao()
    private val productsDao = localDatasource.getProductsDao()
    private val appConfigDao = localDatasource.getAppConfigDao()

    private val dispatcher = AppDispatchers.IO

    suspend fun getAllShoppingLists(): Flow<ShoppingLists> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getAllShoppingLists(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, lastPosition, appConfig ->
                mapping.toShoppingLists(shoppingLists, lastPosition, appConfig)
            }
        )
    }

    suspend fun getPurchases(): Flow<ShoppingLists> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getPurchases(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, lastPosition, appConfig ->
                mapping.toShoppingLists(shoppingLists, lastPosition, appConfig)
            }
        )
    }

    suspend fun getArchive(): Flow<ShoppingLists> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getArchive(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, lastPosition, appConfig ->
                mapping.toShoppingLists(shoppingLists, lastPosition, appConfig)
            }
        )
    }

    suspend fun getTrash(): Flow<ShoppingLists> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getTrash(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, lastPosition, appConfig ->
                mapping.toShoppingLists(shoppingLists, lastPosition, appConfig)
            }
        )
    }

    suspend fun getReminders(): Flow<ShoppingLists> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getReminders(),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingLists, lastPosition, appConfig ->
                mapping.toShoppingLists(shoppingLists, lastPosition, appConfig)
            }
        )
    }

    suspend fun getEditReminder(uid: String?): Flow<EditReminder> = withContext(dispatcher) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().map {
                mapping.toEditReminder(null, it)
            }
        } else {
            shoppingListsDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { shoppingList, appConfig ->
                    mapping.toEditReminder(shoppingList, appConfig)
                }
            )
        }
    }

    suspend fun getNotification(uid: String): Flow<ShoppingListNotification?> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getShoppingList(uid).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { shoppingList, appConfig ->
                if (shoppingList == null) {
                    return@combine null
                }

                mapping.toShoppingListNotification(shoppingList, appConfig)
            }
        )
    }

    suspend fun getNotifications(): Flow<ShoppingListNotifications> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getReminders().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { shoppingLists, appConfig ->
                mapping.toShoppingListNotifications(shoppingLists, appConfig)
            }
        )
    }

    suspend fun getEditShoppingListName(uid: String?): Flow<EditShoppingListName> = withContext(dispatcher) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().map {
                mapping.toEditShoppingListName(null, it)
            }
        } else {
            shoppingListsDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { shoppingList, appConfig ->
                    mapping.toEditShoppingListName(shoppingList, appConfig)
                }
            )
        }
    }

    suspend fun getEditShoppingListTotal(uid: String?): Flow<EditShoppingListTotal> = withContext(dispatcher) {
        return@withContext if (uid == null) {
            appConfigDao.getAppConfig().map {
                mapping.toEditShoppingListTotal(null, it)
            }
        } else {
            shoppingListsDao.getShoppingList(uid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { shoppingList, appConfig ->
                    mapping.toEditShoppingListTotal(shoppingList, appConfig)
                }
            )
        }
    }

    suspend fun getAllProducts(): Flow<List<Product>> = withContext(dispatcher) {
        return@withContext productsDao.getAllProducts().combine(
            flow = appConfigDao.getAppConfig(),
            transform = { products, appConfig ->
                mapping.toProducts(products, appConfig)
            }
        )
    }

    suspend fun getProducts(shoppingUid: String): Flow<Products?> = withContext(dispatcher) {
        return@withContext combine(
            flow = shoppingListsDao.getShoppingList(shoppingUid),
            flow2 = shoppingListsDao.getLastPosition(),
            flow3 = appConfigDao.getAppConfig(),
            transform = { shoppingList, lastPosition, appConfig ->
                if (shoppingList == null) {
                    return@combine null
                }

                mapping.toProducts(shoppingList, lastPosition, appConfig)
            }
        )
    }

    suspend fun getProducts(productUids: List<String>): Flow<List<Product>> = withContext(dispatcher) {
        return@withContext productsDao.getProducts(productUids).combine(
            flow = appConfigDao.getAppConfig(),
            transform = { products, appConfig ->
                mapping.toProducts(products, appConfig)
            }
        )
    }

    suspend fun getAddEditProduct(
        shoppingUid: String,
        productUid: String?
    ): Flow<AddEditProduct> = withContext(dispatcher) {
        return@withContext if (productUid == null) {
            productsDao.getLastPosition(shoppingUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { lastPosition, appConfig ->
                    mapping.toAddEditProduct(null, lastPosition, appConfig)
                }
            )
        } else {
            combine(
                flow = productsDao.getProduct(productUid),
                flow2 = productsDao.getLastPosition(shoppingUid),
                flow3 = appConfigDao.getAppConfig(),
                transform = { product, lastPosition, appConfig ->
                    mapping.toAddEditProduct(product, lastPosition, appConfig)
                }
            )
        }
    }

    suspend fun getCalculateChange(shoppingUid: String?): Flow<CalculateChange> = withContext(dispatcher) {
        return@withContext if (shoppingUid == null) {
            appConfigDao.getAppConfig().map {
                mapping.toCalculateChange(null, it)
            }
        } else {
            shoppingListsDao.getShoppingList(shoppingUid).combine(
                flow = appConfigDao.getAppConfig(),
                transform = { shoppingList, appConfig ->
                    mapping.toCalculateChange(shoppingList, appConfig)
                }
            )
        }
    }

    suspend fun getShoppingListsFirstPosition(): Flow<Int?> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getFirstPosition()
    }

    suspend fun getShoppingListsLastPosition(): Flow<Int?> = withContext(dispatcher) {
        return@withContext shoppingListsDao.getLastPosition()
    }

    suspend fun getProductsFirstPosition(shoppingUid: String): Flow<Int?> = withContext(dispatcher) {
        return@withContext productsDao.getFirstPosition(shoppingUid)
    }

    suspend fun getProductsLastPosition(shoppingUid: String): Flow<Int?> = withContext(dispatcher) {
        return@withContext productsDao.getLastPosition(shoppingUid)
    }

    suspend fun checkIfProductExists(productUid: String): Flow<String?> = withContext(dispatcher) {
        return@withContext productsDao.checkIfProductExists(productUid)
    }

    suspend fun saveShoppingLists(shoppingLists: List<ShoppingList>): Unit = withContext(dispatcher) {
        val shoppings = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.insertShoppings(shoppings)

        val products = mapping.toProductEntitiesFromShoppingLists(shoppingLists)
        productsDao.insertProducts(products)
    }

    suspend fun saveShoppingList(shoppingList: ShoppingList): Unit = withContext(dispatcher) {
        val shopping = mapping.toShoppingEntity(shoppingList)
        shoppingListsDao.insertShopping(shopping)

        val products = mapping.toProductEntitiesFromProducts(shoppingList.products)
        productsDao.insertProducts(products)
    }

    suspend fun swapShoppingLists(shoppingLists: List<ShoppingList>): Unit = withContext(dispatcher) {
        val entities = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.insertShoppings(entities)
    }

    suspend fun swapShoppingLists(first: ShoppingList, second: ShoppingList): Unit = withContext(dispatcher) {
        shoppingListsDao.updatePosition(first.uid, first.position, first.lastModified)
        shoppingListsDao.updatePosition(second.uid, second.position, second.lastModified)
    }

    suspend fun saveShoppingListName(uid: String, name: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.updateName(uid, name, lastModified)
    }

    suspend fun saveReminder(uid: String, reminder: Long, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.updateReminder(uid, reminder, lastModified)
    }

    suspend fun saveShoppingListTotal(uid: String, total: Money, lastModified: Long): Unit = withContext(dispatcher) {
        val value = mapping.toMoneyValue(total)
        shoppingListsDao.updateTotal(uid, value, lastModified)
    }

    suspend fun saveProducts(products: List<Product>): Unit = withContext(dispatcher) {
        val entities = mapping.toProductEntitiesFromProducts(products)
        productsDao.insertProducts(entities)

        val first = products.first()
        shoppingListsDao.updateLastModified(first.shoppingUid, first.lastModified)
    }

    suspend fun saveProduct(product: Product): Unit = withContext(dispatcher) {
        val entity = mapping.toProductEntity(product)
        productsDao.insertProduct(entity)

        shoppingListsDao.updateLastModified(product.shoppingUid, product.lastModified)
    }

    suspend fun swapProducts(first: Product, second: Product): Unit = withContext(dispatcher) {
        productsDao.updatePosition(first.productUid, first.position, first.lastModified)
        productsDao.updatePosition(second.productUid, second.position, second.lastModified)
    }

    suspend fun swapProducts(products: List<Product>): Unit = withContext(dispatcher) {
        products.forEach {
            productsDao.updatePosition(it.productUid, it.position, it.lastModified)
        }
    }

    suspend fun completeProduct(productUid: String, lastModified: Long): Unit = withContext(dispatcher) {
        productsDao.completeProduct(productUid, lastModified)
    }

    suspend fun activeProduct(productUid: String, lastModified: Long): Unit = withContext(dispatcher) {
        productsDao.activeProduct(productUid, lastModified)
    }

    suspend fun pinShoppingLists(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.pinShoppings(uids, lastModified)
    }

    suspend fun unpinShoppingLists(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.unpinShoppings(uids, lastModified)
    }

    suspend fun pinProducts(productsUids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        productsDao.pinProducts(productsUids, lastModified)
    }

    suspend fun unpinProducts(productsUids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        productsDao.unpinProducts(productsUids, lastModified)
    }

    suspend fun moveShoppingListsToPurchases(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToPurchases(uids, lastModified)
    }

    suspend fun moveShoppingListToPurchases(uid: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToPurchases(uid, lastModified)
    }

    suspend fun moveShoppingListsToArchive(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToArchive(uids, lastModified)
    }

    suspend fun moveShoppingListToArchive(uid: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToArchive(uid, lastModified)
    }

    suspend fun moveShoppingListsToTrash(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToTrash(uids, lastModified)
    }

    suspend fun moveShoppingListToTrash(uid: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.moveToTrash(uid, lastModified)
    }

    suspend fun copyShoppingLists(shoppingLists: List<ShoppingList>): Unit = withContext(dispatcher) {
        saveShoppingLists(shoppingLists)
    }

    suspend fun copyShoppingList(shoppingList: ShoppingList): Unit = withContext(dispatcher) {
        saveShoppingList(shoppingList)
    }

    suspend fun sortProductsBy(
        shoppingUid: String,
        sortBy: SortBy,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        val name = mapping.toSortByName(sortBy)
        shoppingListsDao.sortBy(shoppingUid, name, lastModified)
    }

    suspend fun sortProductsAscending(
        shoppingUid: String,
        sortAscending: Boolean,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        shoppingListsDao.sortAscending(shoppingUid, sortAscending, lastModified)
    }

    suspend fun enableAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        val sortAscending = sort.ascending
        shoppingListsDao.enableAutomaticSorting(shoppingUid, sortBy, sortAscending, lastModified)
    }

    suspend fun disableAutomaticSorting(
        shoppingUid: String,
        sort: Sort,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        val sortBy = mapping.toSortByName(sort.sortBy)
        val sortAscending = sort.ascending
        shoppingListsDao.disableAutomaticSorting(shoppingUid, sortBy, sortAscending, lastModified)
    }

    suspend fun deleteAllShoppingLists(): Unit = withContext(dispatcher) {
        shoppingListsDao.deleteAllShoppings()
        productsDao.deleteAllProducts()
    }

    suspend fun deleteShoppingLists(shoppingLists: List<ShoppingList>): Unit = withContext(dispatcher) {
        val shoppings = mapping.toShoppingEntities(shoppingLists)
        shoppingListsDao.deleteShoppings(shoppings)

        val shoppingUids = mapping.toShoppingUids(shoppingLists)
        productsDao.deleteProductsByShoppingUids(shoppingUids)
    }

    suspend fun deleteShoppingListsByUids(uids: List<String>): Unit = withContext(dispatcher) {
        shoppingListsDao.deleteShoppingsByUids(uids)
        productsDao.deleteProductsByShoppingUids(uids)
    }

    suspend fun deleteProductsByShoppingUid(
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        shoppingListsDao.updateLastModified(shoppingUid, lastModified)
        productsDao.deleteProductsByShoppingUid(shoppingUid)
    }

    suspend fun deleteProductsByProductUids(
        productsUids: List<String>,
        shoppingUid: String,
        lastModified: Long
    ): Unit = withContext(dispatcher) {
        shoppingListsDao.updateLastModified(shoppingUid, lastModified)
        productsDao.deleteProductsByProductUids(productsUids)
    }

    suspend fun deleteReminders(uids: List<String>, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.deleteReminders(uids, lastModified)
    }

    suspend fun deleteReminder(uid: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.deleteReminder(uid, lastModified)
    }

    suspend fun deleteShoppingListTotal(uid: String, lastModified: Long): Unit = withContext(dispatcher) {
        shoppingListsDao.deleteTotal(uid, lastModified)
    }
}