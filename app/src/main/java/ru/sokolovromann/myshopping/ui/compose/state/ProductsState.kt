package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.calculateTotalToText
import ru.sokolovromann.myshopping.ui.utils.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.utils.getProductsItems
import java.util.*

class ProductsState {

    var screenData by mutableStateOf(ProductsScreenData())
        private set

    var editCompleted by mutableStateOf(false)
        private set

    private var shareTotal by mutableStateOf("")

    private var products by mutableStateOf(Products())

    fun showLoading() {
        screenData = ProductsScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: ProductPreferences, shoppingListName: String, reminder: Long?) {
        screenData = ProductsScreenData(
            screenState = ScreenState.Nothing,
            shoppingListName = UiText.FromString(shoppingListName),
            reminderText = toReminderText(reminder),
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize
        )

        editCompleted = preferences.editCompleted
        products = Products(preferences = preferences)
    }

    fun showProducts(products: Products) {
        val preferences = products.preferences
        val totalText = if (preferences.displayMoney) {
            products.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val shoppingListName: UiText = if (products.formatName().isEmpty()) {
            UiText.Nothing
        } else {
            UiText.FromString(products.formatName())
        }

        screenData = ProductsScreenData(
            screenState = ScreenState.Showing,
            shoppingListName = shoppingListName,
            products = products.getProductsItems(),
            totalText = totalText,
            reminderText = toReminderText(products.shoppingList.reminder),
            multiColumns = preferences.multiColumns,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize
        )

        editCompleted = preferences.editCompleted

        var total = 0f
        products.shoppingList.products.forEach {
            if (!it.completed) {
                total += it.calculateTotal().value
            }
        }
        shareTotal = if (preferences.displayMoney && total > 0f) {
            Money(total, preferences.currency).toString()
        } else {
            ""
        }

        this.products = products
    }

    fun showProductMenu(uid: String) {
        screenData = screenData.copy(productMenuUid = uid)
    }

    fun showProductsMenu() {
        screenData = screenData.copy(showProductsMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(
            showSort = true,
            showProductsMenu = false
        )
    }

    fun showDisplayTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun hideProductMenu() {
        screenData = screenData.copy(productMenuUid = null)
    }

    fun hideProductsMenu() {
        screenData = screenData.copy(showProductsMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideDisplayTotal() {
        screenData = screenData.copy(showDisplayTotal = false)
    }

    fun getShareProductsResult(): Result<String> {
        var shareText = if (screenData.shoppingListName is UiText.FromString) {
            "${(screenData.shoppingListName as UiText.FromString).value}:\n"
        } else {
            ""
        }

        screenData.products.forEach {
            if (!it.completed) {
                val name = if (it.nameText is UiText.FromString) it.nameText.value else ""
                val body = if (it.bodyText is UiText.FromString) " • ${it.bodyText.value}" else ""
                shareText += "- $name$body\n"
            }
        }

        if (shareTotal.isEmpty()) {
            if (shareText.isNotEmpty()) {
                shareText = shareText.dropLast(1)
            }
        } else {
            shareText += "\n= $shareTotal"
        }

        return Result.success(shareText)
    }

    fun getProductsUpResult(uid: String): Result<Pair<Product, Product>> {
        val sortProducts = products.sortProducts()
        return if (sortProducts.size < 2) {
            Result.failure(Exception())
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in sortProducts.indices) {
                val product = sortProducts[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = sortProducts[currentIndex].copy(
                position = sortProducts[previousIndex].position,
                lastModified = lastModified
            )
            val previousProduct = sortProducts[previousIndex].copy(
                position = sortProducts[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, previousProduct)
            Result.success(success)
        }
    }

    fun getProductsDownResult(uid: String): Result<Pair<Product, Product>> {
        val sortProducts = products.sortProducts()
        return if (sortProducts.size < 2) {
            Result.failure(Exception())
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in sortProducts.indices) {
                val product = sortProducts[index]

                currentIndex = index
                if (index < sortProducts.lastIndex) {
                    nextIndex = index + 1
                }

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = sortProducts[currentIndex].copy(
                position = sortProducts[nextIndex].position,
                lastModified = lastModified
            )
            val nextProduct = sortProducts[nextIndex].copy(
                position = sortProducts[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, nextProduct)
            Result.success(success)
        }
    }

    fun sortProductsResult(sortBy: SortBy): Result<List<Product>> {
        val productsList = products.shoppingList.products
        return if (productsList.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = when (sortBy) {
                SortBy.CREATED -> productsList.sortedBy { it.created }
                SortBy.LAST_MODIFIED -> productsList.sortedBy { it.lastModified }
                SortBy.NAME -> productsList.sortedBy { it.name }
                SortBy.TOTAL -> productsList.sortedBy { it.calculateTotal().value }
            }.mapIndexed { index, product ->
                product.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    private fun toReminderText(reminder: Long?): UiText {
        return if (reminder == null) {
            UiText.Nothing
        } else {
            Calendar.getInstance()
                .apply { timeInMillis = reminder }
                .getDisplayDateAndTime()
        }
    }
}

data class ProductsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingListName: UiText = UiText.Nothing,
    val products: List<ProductItem> = listOf(),
    val productMenuUid: String? = null,
    val showProductsMenu: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val reminderText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val showSort: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)