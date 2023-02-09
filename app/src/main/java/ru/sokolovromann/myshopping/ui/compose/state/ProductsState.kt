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

    private var products: List<Product> by mutableStateOf(listOf())

    fun showLoading() {
        screenData = ProductsScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: ProductPreferences, shoppingListName: String, reminder: Long?) {
        screenData = ProductsScreenData(
            screenState = ScreenState.Nothing,
            shoppingListName = UiText.FromString(shoppingListName),
            reminderText = toReminderText(reminder),
            sort = preferences.sort,
            displayTotal = preferences.displayTotal,
            fontSize = preferences.fontSize
        )

        editCompleted = preferences.editCompleted
        products = listOf()
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
            sort = preferences.sort,
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

        this.products = products.sortProducts()
    }

    fun showProductMenu(uid: String) {
        screenData = screenData.copy(productMenuUid = uid)
    }

    fun showProductsMenu() {
        screenData = screenData.copy(showProductsMenu = true)
    }

    fun showSort() {
        screenData = screenData.copy(showSort = true)
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

    fun getProductsToUpResult(uid: String): Result<Pair<Product, Product>> {
        return if (products.size < 2) {
            Result.failure(Exception())
        } else {
            var previousIndex = 0
            var currentIndex = 0
            for (index in products.indices) {
                val product = products[index]
                if (currentIndex > 0) {
                    previousIndex = index - 1
                }
                currentIndex = index

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = products[currentIndex].copy(
                position = products[previousIndex].position,
                lastModified = lastModified
            )
            val previousProduct = products[previousIndex].copy(
                position = products[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, previousProduct)
            Result.success(success)
        }
    }

    fun getProductsToDownResult(uid: String): Result<Pair<Product, Product>> {
        return if (products.size < 2) {
            Result.failure(Exception())
        } else {
            var currentIndex = 0
            var nextIndex = 0
            for (index in products.indices) {
                val product = products[index]

                currentIndex = index
                if (index < products.lastIndex) {
                    nextIndex = index + 1
                }

                if (product.productUid == uid) {
                    break
                }
            }

            val lastModified = System.currentTimeMillis()
            val currentProduct = products[currentIndex].copy(
                position = products[nextIndex].position,
                lastModified = lastModified
            )
            val nextProduct = products[nextIndex].copy(
                position = products[currentIndex].position,
                lastModified = lastModified
            )

            val success = Pair(currentProduct, nextProduct)
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
    val sort: Sort = Sort(),
    val showSort: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)