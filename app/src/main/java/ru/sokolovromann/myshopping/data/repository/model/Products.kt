package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import java.util.Calendar
import java.util.UUID

@Deprecated("USe ShoppingListWithConfig")
data class Products(
    private val shoppingList: ShoppingList = ShoppingList(),
    private val shoppingListsLastPosition: Int? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val userPreferences = appConfig.userPreferences

    fun copyShoppingList(): Result<ShoppingList> {
        val shoppingUid = UUID.randomUUID().toString()
        val created = System.currentTimeMillis()
        val success = shoppingList.copy(
            id = 0,
            position = nextShoppingListsPosition(),
            uid = shoppingUid,
            created = created,
            lastModified = created,
            products = copyProducts(shoppingUid, created)
        )
        return Result.success(success)
    }

    fun sortProducts(sort: Sort): Result<List<Product>> {
        val sorted = shoppingList.products.sortProducts(sort)
        return if (sorted.isEmpty()) {
            val exception = UnsupportedOperationException("Sort empty list is not supported")
            Result.failure(exception)
        } else {
            val success = sorted.mapIndexed { index, product ->
                product.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun reverseSortProducts(): Result<List<Product>> {
        val sortPinnedProducts = getActivePinnedProducts().reversed()
        val sortOtherProducts = getOtherProducts().reversed()
        return if (sortPinnedProducts.isEmpty() && sortOtherProducts.isEmpty()) {
            val exception = UnsupportedOperationException("Sort empty list is not supported")
            Result.failure(exception)
        } else {
            val success = sortPinnedProducts
                .toMutableList()
                .apply { addAll(sortOtherProducts) }
                .mapIndexed { index, product ->
                    product.copy(
                        position = index,
                        lastModified = System.currentTimeMillis()
                    )
                }
            Result.success(success)
        }
    }

    fun moveProductUp(uid: String): Result<Pair<Product, Product>> {
        val pinned = isProductPinned(uid)
        val formatProducts = if (pinned) getActivePinnedProducts() else getOtherProducts()

        return if (formatProducts.size < 2) {
            val exception = UnsupportedOperationException("Move if products size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in formatProducts.indices) {
                val product = formatProducts[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = formatProducts[currentIndex].copy(
                position = formatProducts[previousIndex].position,
                lastModified = lastModified
            )
            val previousProduct = formatProducts[previousIndex].copy(
                position = formatProducts[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, previousProduct)
            Result.success(success)
        }
    }

    fun moveProductDown(uid: String): Result<Pair<Product, Product>> {
        val pinned = isProductPinned(uid)
        val formatProducts = if (pinned) getActivePinnedProducts() else getOtherProducts()

        return if (formatProducts.size < 2) {
            val exception = UnsupportedOperationException("Move if products size less than 2 is not supported")
            Result.failure(exception)
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in formatProducts.indices) {
                val product = formatProducts[index]

                currentIndex = index
                if (index < formatProducts.lastIndex) {
                    nextIndex = index + 1
                }

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = formatProducts[currentIndex].copy(
                position = formatProducts[nextIndex].position,
                lastModified = lastModified
            )
            val nextProduct = formatProducts[nextIndex].copy(
                position = formatProducts[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, nextProduct)
            Result.success(success)
        }
    }

    fun getShoppingListUid(): String {
        return shoppingList.uid
    }

    fun getActivePinnedProducts(): List<Product> {
        val sort = if (shoppingList.sortFormatted) shoppingList.sort else Sort()
        val sorted = getProductsAsPair().first.sortProducts(sort)
        val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
        return if (noSplit) sorted else sorted.splitProducts(userPreferences.displayCompleted)
    }

    fun getOtherProducts(): List<Product> {
        val sort = if (shoppingList.sortFormatted) shoppingList.sort else Sort()
        val sorted = getProductsAsPair().second.sortProducts(sort)

        val forcedDisplayHide = getShoppingLocation() == ShoppingLocation.TRASH &&
                getDisplayCompleted() == DisplayCompleted.HIDE
        val displayCompleted = if (forcedDisplayHide) DisplayCompleted.LAST else userPreferences.displayCompleted
        val noSplit = displayCompleted == DisplayCompleted.NO_SPLIT

        return if (noSplit) sorted else sorted.splitProducts(displayCompleted)
    }

    fun getProductUids(): List<String> {
        return shoppingList.products.map { it.productUid }
    }

    fun getShareText(): Result<String> {
        return if (isProductsEmpty()) {
            val exception = IllegalArgumentException("You have no products")
            Result.failure(exception)
        } else {
            val success = StringBuilder()

            val displayName = getDisplayName().isNotEmpty()
            if (displayName) {
                success.append(getDisplayName())
                success.append(":\n")
            }

            getProductsAsList().forEach {
                if (!it.completed) {
                    success.append("- ")

                    val title = productToTitle(it)
                    success.append(title)

                    val body = productToBody(it)
                    if (body.isNotEmpty()) {
                        success.append(getSeparator())
                        success.append(body)
                    }

                    success.append("\n")
                }
            }

            val total = calculateTotal(DisplayTotal.COMPLETED)
            if (displayMoney() && total.isNotEmpty()) {
                success.append("\n=")
                success.append(total.getDisplayValue())
            } else {
                if (success.isNotEmpty()) {
                    success.dropLast(1)
                }
            }

            Result.success(success.toString())
        }
    }

    fun getDisplayName(): String {
        return shoppingList.name
    }

    fun calculateTotal(
        displayTotal: DisplayTotal = userPreferences.displayTotal,
    ): Money {
        return if (shoppingList.totalFormatted) {
            shoppingList.total
        } else {
            var all = 0f
            var completed = 0f
            var active = 0f

            shoppingList.products.forEach { product ->
                val totalValue = product.formatTotal().getFormattedValueWithoutSeparators().toFloat()

                all += totalValue
                if (product.completed) {
                    completed += totalValue
                } else {
                    active += totalValue
                }
            }

            val total = when (displayTotal) {
                DisplayTotal.ALL -> all
                DisplayTotal.COMPLETED -> completed
                DisplayTotal.ACTIVE -> active
            }

            Money(total, userPreferences.currency, false, userPreferences.moneyDecimalFormat)
        }
    }

    fun calculateTotal(uids: List<String>): Money {
        var total = 0f

        shoppingList.products.forEach { product ->
            val totalValue = product.formatTotal().getFormattedValueWithoutSeparators().toFloat()
            if (uids.contains(product.productUid)) {
                total += totalValue
            }
        }

        return Money(total, userPreferences.currency, false, userPreferences.moneyDecimalFormat)
    }

    fun getCalendarReminder(): Calendar? {
        val reminder = shoppingList.reminder
        return if (reminder == null) {
            null
        } else {
            Calendar.getInstance().apply { timeInMillis = reminder }
        }
    }

    fun getSort(): Sort {
        return shoppingList.sort
    }

    fun getShoppingLocation(): ShoppingLocation {
        return shoppingList.location
    }

    fun isTotalFormatted(): Boolean {
        return shoppingList.totalFormatted
    }

    fun isAutomaticSorting(): Boolean {
        return shoppingList.sortFormatted
    }

    fun isCompleted(): Boolean {
        return shoppingList.completed
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun getDisplayCompleted(): DisplayCompleted {
        return userPreferences.displayCompleted
    }

    fun getDisplayTotal(): DisplayTotal {
        return userPreferences.displayTotal
    }

    fun displayMoney(): Boolean {
        return userPreferences.displayMoney
    }

    fun displayOtherFields(): Boolean {
        return userPreferences.displayOtherFields
    }

    fun editProductAfterCompleted(): Boolean {
        return userPreferences.editProductAfterCompleted
    }

    fun isMultiColumns(): Boolean {
        return userPreferences.productsMultiColumns
    }

    fun isCompletedWithCheckbox(): Boolean {
        return userPreferences.completedWithCheckbox
    }

    fun isColoredCheckbox(): Boolean {
        return userPreferences.coloredCheckbox
    }

    fun isSmartphoneScreen(): Boolean {
        return appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
    }

    fun isDisplayHiddenProducts(): Boolean {
        val notTrashLocation = getShoppingLocation() != ShoppingLocation.TRASH
        val hideCompleted = userPreferences.displayCompleted == DisplayCompleted.HIDE
        val hasHiddenProducts = shoppingList.products.splitProducts(DisplayCompleted.FIRST).first().completed
        return notTrashLocation && hideCompleted && hasHiddenProducts
    }

    private fun isProductPinned(productUid: String): Boolean {
        return getActivePinnedProducts().find { it.productUid == productUid } != null
    }

    fun isProductsEmpty(): Boolean {
        return shoppingList.products.isEmpty()
    }

    fun productToTitle(product: Product): String {
        val builder = StringBuilder(product.name)

        val displayBrand = displayOtherFields() && product.brand.isNotEmpty()
        if (displayBrand) {
            builder.append(" ${product.brand}")
        }

        val displayManufacturer = displayOtherFields() && product.manufacturer.isNotEmpty()
        if (displayManufacturer) {
            builder.append("${getSeparator()}${product.manufacturer}")
        }

        return builder.toString()
    }

    fun productToBody(product: Product): String {
        val builder = StringBuilder(product.size)

        val displayColor = displayOtherFields() && product.color.isNotEmpty()
        if (displayColor) {
            if (builder.isNotEmpty()) {
                builder.append(getSeparator())
            }
            builder.append(product.color)
        }

        if (builder.isNotEmpty()) {
            builder.append(getSeparator())
        }

        val displayQuantity = product.quantity.isNotEmpty()
        val displayPrice = displayMoney() && !isTotalFormatted() && product.formatTotal().isNotEmpty()


        if (displayPrice) {
            if (displayQuantity) {
                builder.append(product.quantity)
                builder.append(getSeparator())
            }
            builder.append(product.formatTotal())
        } else {
            if (displayQuantity) {
                builder.append(product.quantity)
            }
        }

        val displayNote = product.note.isNotEmpty()
        if (displayNote) {
            if (builder.isNotEmpty()) {
                builder.append("\n")
            }
            builder.append(product.note)
        }

        return builder.toString()
    }

    private fun getProductsAsList(): List<Product> {
        val pinned = getProductsAsPair().first
        val other = getProductsAsPair().second
        return pinned.toMutableList()
            .apply { addAll(other) }
            .toList()
    }

    private fun getProductsAsPair(): Pair<List<Product>, List<Product>> {
        return shoppingList.products.partition {
            val noSplit = userPreferences.displayCompleted == DisplayCompleted.NO_SPLIT
            if (noSplit) it.pinned else it.pinned && !it.completed
        }
    }

    private fun getSeparator(): String {
        return userPreferences.purchasesSeparator
    }

    private fun nextShoppingListsPosition(): Int {
        return shoppingListsLastPosition?.plus(1) ?: 0
    }

    private fun copyProducts(shoppingUid: String, created: Long): List<Product> {
        return shoppingList.products.map {
            it.copy(
                id = 0,
                shoppingUid = shoppingUid,
                productUid = UUID.randomUUID().toString(),
                created = created,
                lastModified = created
            )
        }
    }
}