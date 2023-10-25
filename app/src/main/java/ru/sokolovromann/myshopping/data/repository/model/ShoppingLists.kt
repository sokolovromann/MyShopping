package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import java.util.UUID

@Deprecated("Use ShoppingListsWithConfig")
data class ShoppingLists(
    private val shoppingLists: List<ShoppingList> = listOf(),
    private val shoppingListsLastPosition: Int? = null,
    private val shoppingLocation: ShoppingLocation? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    companion object {
        const val UNNAMED = "ShoppingLists.NO_NAME"
        const val MAX_PRODUCTS_SIGN = "ShoppingLists.MAX_PRODUCTS_SIGN"
    }

    private val userPreferences = appConfig.userPreferences

    fun createShoppingList(): Result<ShoppingList> {
        return if (shoppingListsLastPosition == null) {
            val exception = UnsupportedOperationException("Last position is not available")
            Result.failure(exception)
        } else {
            val success = ShoppingList(position = nextShoppingListsPosition())
            Result.success(success)
        }
    }

    fun copyShoppingLists(uids: List<String>?): Result<List<ShoppingList>> {
        return if (uids.isNullOrEmpty()) {
            val exception = InvalidUidException("Uids must not be bull or empty")
            Result.failure(exception)
        } else {
            val success = mutableListOf<ShoppingList>()
            uids.forEach { uid ->
                val shoppingUid = UUID.randomUUID().toString()
                val created = System.currentTimeMillis()
                val selectedShoppingList = shoppingLists.find { it.uid == uid } ?: ShoppingList()
                val newShoppingList = selectedShoppingList.copy(
                    id = 0,
                    position = nextShoppingListsPosition(),
                    uid = shoppingUid,
                    created = created,
                    lastModified = created,
                    products = selectedShoppingList.products.map {
                        it.copy(
                            id = 0,
                            shoppingUid = shoppingUid,
                            productUid = UUID.randomUUID().toString(),
                            created = created,
                            lastModified = created
                        )
                    }
                )
                success.add(newShoppingList)
            }
            Result.success(success)
        }
    }

    fun sortShoppingLists(sort: Sort): Result<List<ShoppingList>> {
        val sorted = shoppingLists.sortShoppingLists(sort)
        return if (sorted.isEmpty()) {
            val exception = UnsupportedOperationException("Sort empty list is not supported")
            Result.failure(exception)
        } else {
            val success = sorted.mapIndexed { index, shoppingList ->
                shoppingList.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun reverseSortShoppingLists(): Result<List<ShoppingList>> {
        val sortPinnedShoppingLists = getActivePinnedShoppingLists().reversed()
        val sortOtherShoppingLists = getOtherShoppingLists().reversed()
        return if (sortPinnedShoppingLists.isEmpty() && sortOtherShoppingLists.isEmpty()) {
            val exception = UnsupportedOperationException("Sort empty list is not supported")
            Result.failure(exception)
        } else {
            val success = sortPinnedShoppingLists
                .toMutableList()
                .apply { addAll(sortOtherShoppingLists) }
                .mapIndexed { index, shoppingList ->
                    shoppingList.copy(
                        position = index,
                        lastModified = System.currentTimeMillis()
                    )
                }
            Result.success(success)
        }
    }

    fun moveShoppingListUp(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        return if (shoppingLists.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in shoppingLists.indices) {
                val shoppingList = shoppingLists[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = shoppingLists[currentIndex].copy(
                position = shoppingLists[previousIndex].position,
                lastModified = lastModified
            )
            val previousShoppingList = shoppingLists[previousIndex].copy(
                position = shoppingLists[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, previousShoppingList)
            Result.success(success)
        }
    }

    fun moveShoppingListDown(uid: String): Result<Pair<ShoppingList, ShoppingList>> {
        return if (shoppingLists.size < 2) {
            val exception = UnsupportedOperationException("Move if shopping lists size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in shoppingLists.indices) {
                val shoppingList = shoppingLists[index]

                currentIndex = index
                if (index < shoppingLists.lastIndex) {
                    nextIndex = index + 1
                }

                if (shoppingList.uid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentShoppingList = shoppingLists[currentIndex].copy(
                position = shoppingLists[nextIndex].position,
                lastModified = lastModified
            )
            val nextShoppingList = shoppingLists[nextIndex].copy(
                position = shoppingLists[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentShoppingList, nextShoppingList)
            Result.success(success)
        }
    }

    fun getShoppingList(uid: String): Result<ShoppingList> {
        val shoppingList = shoppingLists.find { it.uid == uid }
        return if (shoppingList == null) {
            val exception = InvalidUidException("Uid is not exists")
            Result.failure(exception)
        } else {
            Result.success(shoppingList)
        }
    }

    fun copyProducts(shoppingUid: String?, products: List<Product>): Result<List<Product>> {
        return if (shoppingUid.isNullOrEmpty()) {
            val exception = InvalidUidException("Uid must not be null or empty")
            Result.failure(exception)
        } else {
            val position = getAllShoppingLists()
                .find { it.uid == shoppingUid }?.nextProductsPosition() ?: 0
            val success = products.mapIndexed { index, product ->
                val newPosition = position + index
                Product(
                    position = newPosition,
                    shoppingUid = shoppingUid,
                    name = product.name,
                    quantity = product.quantity,
                    price = product.price,
                    discount = product.discount,
                    taxRate = product.taxRate,
                    completed = product.completed
                )
            }

            Result.success(success)
        }
    }

    fun moveProducts(shoppingUid: String?, products: List<Product>): Result<List<Product>> {
        return if (shoppingUid.isNullOrEmpty()) {
            val exception = InvalidUidException("Uid must not be null or empty")
            Result.failure(exception)
        } else {
            val position = getAllShoppingLists()
                .find { it.uid == shoppingUid }?.nextProductsPosition() ?: 0
            val success = products.mapIndexed { index, product ->
                val newPosition = position + index
                product.copy(
                    position = newPosition,
                    shoppingUid = shoppingUid,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun getAllShoppingLists(): List<ShoppingList> {
        val allShoppingLists = shoppingListsToPair()
        return allShoppingLists.first.toMutableList()
            .apply { addAll(allShoppingLists.second) }
            .toList()
    }

    fun getActivePinnedShoppingLists(): List<ShoppingList> {
        val sorted = shoppingListsToPair().first
            .map {
                val sortedProducts = getSortedProducts(it.products, it.sort, it.sortFormatted)
                it.copy(products = sortedProducts)
            }
            .sortShoppingLists()
        val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
        return if (noSplit) {
            sorted
        } else {
            sorted.splitShoppingLists(userPreferences.displayCompleted)
        }
    }

    fun getOtherShoppingLists(): List<ShoppingList> {
        val sorted = shoppingListsToPair().second
            .map {
                val sortedProducts = getSortedProducts(it.products, it.sort, it.sortFormatted)
                it.copy(products = sortedProducts)
            }
            .sortShoppingLists()
        val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
        return if (noSplit) {
            sorted
        } else {
            sorted.splitShoppingLists(userPreferences.displayCompleted)
        }
    }

    fun getUids(): List<String> {
        return shoppingLists.map { it.uid }
    }

    fun calculateTotal(): Money {
        var total = 0f
        shoppingLists.forEach {
            total += if (it.totalFormatted && userPreferences.displayTotal == DisplayTotal.ALL) {
                it.calculateTotal(false).value
            } else {
                it.calculateTotal(true).value
            }
        }
        return Money(total, userPreferences.currency, false, userPreferences.moneyDecimalFormat)
    }

    fun getDisplayProducts(): DisplayProducts {
        return userPreferences.displayShoppingsProducts
    }

    fun displayMoney(): Boolean {
        return userPreferences.displayMoney
    }

    fun displayHiddenShoppingLists(): Boolean {
        val notTrashLocation = shoppingLocation != ShoppingLocation.TRASH
        val hideCompleted = userPreferences.displayCompleted == DisplayCompleted.HIDE
        val hasHiddenShoppingList = shoppingLists.splitShoppingLists(DisplayCompleted.FIRST).first().completed
        return notTrashLocation && hideCompleted && hasHiddenShoppingList
    }

    fun getDisplayCompleted(): DisplayCompleted {
        return userPreferences.displayCompleted
    }

    fun getDisplayTotal(): DisplayTotal {
        return userPreferences.displayTotal
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun isNightTheme(): Boolean {
        return userPreferences.nightTheme
    }

    fun isMultiColumns(): Boolean {
        return userPreferences.shoppingsMultiColumns
    }

    fun isColoredCheckbox(): Boolean {
        return userPreferences.coloredCheckbox
    }

    fun isSmartphoneScreen(): Boolean {
        return appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
    }

    fun isShoppingListsEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }

    fun calculateTotal(uids: List<String>): Money {
        var total = 0f
        shoppingLists.forEach {
            if (uids.contains(it.uid)) {
                total += if (it.totalFormatted && userPreferences.displayTotal == DisplayTotal.ALL) {
                    it.calculateTotal(false).value
                } else {
                    it.calculateTotal(true).value
                }
            }
        }

        return Money(total, userPreferences.currency, false, userPreferences.moneyDecimalFormat)
    }

    fun shoppingListToName(shoppingList: ShoppingList): String {
        val displayUnnamed = getDisplayProducts() == DisplayProducts.HIDE && shoppingList.name.isEmpty()
        return if (displayUnnamed) UNNAMED else shoppingList.name
    }

    fun shoppingListToProducts(shoppingList: ShoppingList): List<Pair<Boolean?, String>> {
        val maxShoppingProducts = 10
        val totalFormatted = shoppingList.totalFormatted && getDisplayTotal() == DisplayTotal.ALL

        return if (shoppingList.products.isEmpty()) {
            listOf()
        } else {
            val products: MutableList<Pair<Boolean?, String>> = shoppingList.products
                .filterIndexed { index, _ -> index < maxShoppingProducts}
                .map { product -> productsToPair(product, totalFormatted) }
                .toMutableList()

            if (shoppingList.products.size > maxShoppingProducts) {
                products.add(Pair(null, MAX_PRODUCTS_SIGN))
            }

            products.toList()
        }
    }

    private fun productsToPair(product: Product, totalFormatted: Boolean): Pair<Boolean, String> {
        val builder = StringBuilder(product.name)

        val shortText = isMultiColumns() && isSmartphoneScreen()
        val displayVertically = userPreferences.displayShoppingsProducts == DisplayProducts.VERTICAL
        val displayProductElements = !shortText && displayVertically

        if (displayProductElements) {
            val displayBrand = displayOtherFields() && product.brand.isNotEmpty()
            if (displayBrand) {
                builder.append(getSeparator())
                builder.append(product.brand)
            }

            val displayQuantity = product.quantity.isNotEmpty()
            val displayPrice = displayMoney() && !totalFormatted && product.formatTotal().isNotEmpty()

            if (displayPrice) {
                builder.append(getSeparator())
                if (displayQuantity) {
                    builder.append(product.quantity)
                    builder.append(getSeparator())
                }
                builder.append(product.formatTotal())
            } else {
                if (displayQuantity) {
                    builder.append(getSeparator())
                    builder.append(product.quantity)
                }
            }

            val displayNote = product.note.isNotEmpty()
            if (displayNote) {
                builder.append(getSeparator())
                builder.append(product.note)
            }
        }

        return Pair(
            first = product.completed,
            second = builder.toString()
        )
    }

    private fun shoppingListsToPair(): Pair<List<ShoppingList>, List<ShoppingList>> {
        return shoppingLists.partition {
            val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
            if (noSplit) it.pinned else it.pinned && !it.completed
        }
    }

    private fun getSortedProducts(
        product: List<Product>,
        shoppingSort: Sort,
        shoppingSortFormatted: Boolean
    ): List<Product> {
        val sort = if (shoppingSortFormatted) shoppingSort else Sort()
        val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT

        val productsPartition = product
            .sortProducts(sort)
            .partition { if (noSplit) it.pinned else it.pinned && !it.completed }
        val sorted = productsPartition.first.toMutableList()
            .apply { addAll(productsPartition.second) }

        return if (noSplit) {
            sorted
        } else {
            sorted.splitProducts(userPreferences.displayCompleted)
        }
    }

    private fun getSeparator(): String {
        return userPreferences.purchasesSeparator
    }

    private fun displayOtherFields(): Boolean {
        return userPreferences.displayOtherFields
    }

    private fun nextShoppingListsPosition(): Int {
        return shoppingListsLastPosition?.plus(1) ?: 0
    }
}