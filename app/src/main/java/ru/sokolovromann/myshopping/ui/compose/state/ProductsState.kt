package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.*
import java.util.*

class ProductsState {

    private var products by mutableStateOf(Products())

    private var savedSelectedUid by mutableStateOf("")

    var screenData by mutableStateOf(ProductsScreenData())
        private set

    var editCompleted by mutableStateOf(false)
        private set

    var shoppingListUid by mutableStateOf("")
        private set

    fun showLoading() {
        screenData = ProductsScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(products: Products) {
        this.products = products
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
        val location = products.shoppingList.getShoppingListLocation()
        screenData = ProductsScreenData(
            screenState = ScreenState.Nothing,
            shoppingListName = shoppingListName,
            shoppingListLocation = location,
            shoppingListCompleted = products.isCompleted(),
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            reminderText = toReminderText(products.shoppingList.reminder),
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            fontSize = preferences.fontSize,
            displayMoney = preferences.displayMoney,
            completedWithCheckbox = preferences.completedWithCheckbox
        )

        editCompleted = preferences.editProductAfterCompleted
        shoppingListUid = products.shoppingList.uid
    }

    fun showProducts(products: Products) {
        this.products = products
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

        val location = products.shoppingList.getShoppingListLocation()

        val showHiddenProducts = location != ShoppingListLocation.TRASH
                && preferences.displayCompletedPurchases == DisplayCompleted.HIDE
                && products.hasHiddenProducts()

        val productsItems = if (location == ShoppingListLocation.TRASH) {
            when (preferences.displayCompletedPurchases) {
                DisplayCompleted.FIRST, DisplayCompleted.LAST -> products.getProductsItems()
                else -> products.getProductsItems(DisplayCompleted.LAST)
            }
        } else {
            products.getProductsItems()
        }

        val selectedUids = if (savedSelectedUid.isEmpty()) {
            null
        } else {
            listOf(savedSelectedUid)
        }

        screenData = ProductsScreenData(
            screenState = ScreenState.Showing,
            shoppingListName = shoppingListName,
            shoppingListLocation = location,
            shoppingListCompleted = products.isCompleted(),
            products = productsItems,
            productsNotFoundText = toProductNotFoundText(location),
            totalText = totalText,
            reminderText = toReminderText(products.shoppingList.reminder),
            multiColumns = preferences.productsMultiColumns,
            smartphoneScreen = preferences.smartphoneScreen,
            displayTotal = preferences.displayPurchasesTotal,
            showHiddenProducts = showHiddenProducts,
            fontSize = preferences.fontSize,
            displayMoney = preferences.displayMoney,
            completedWithCheckbox = preferences.completedWithCheckbox,
            selectedUids = selectedUids
        )

        editCompleted = preferences.editProductAfterCompleted
        shoppingListUid = products.shoppingList.uid
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

    fun displayHiddenProducts() {
        screenData = screenData.copy(
            products = products.getProductsItems(DisplayCompleted.LAST),
            showHiddenProducts = false
        )
    }

    fun selectDisplayPurchasesTotal() {
        screenData = screenData.copy(showDisplayTotal = true)
    }

    fun selectProduct(uid: String) {
        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { add(uid) }

        val totalText = if (products.preferences.displayMoney) {
            products.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun selectAllProducts() {
        val uids = products.shoppingList.products.map { it.productUid }

        val totalText = if (products.preferences.displayMoney) {
            products.calculateTotalToText(uids)
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = uids
        )
    }

    fun unselectProduct(uid: String) {
        savedSelectedUid = ""

        val uids = (screenData.selectedUids?.toMutableList() ?: mutableListOf())
            .apply { remove(uid) }
        val checkedUids = if (uids.isEmpty()) null else uids

        val totalText = if (products.preferences.displayMoney) {
            if (checkedUids == null) {
                products.calculateTotalToText()
            } else {
                products.calculateTotalToText(checkedUids)
            }
        } else {
            UiText.Nothing
        }

        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = checkedUids
        )
    }

    fun unselectAllProducts() {
        val totalText = if (products.preferences.displayMoney) {
            products.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        savedSelectedUid = ""
        screenData = screenData.copy(
            totalText = totalText,
            selectedUids = null
        )
    }

    fun hideProductsMenu() {
        screenData = screenData.copy(showProductsMenu = false)
    }

    fun hideSort() {
        screenData = screenData.copy(showSort = false)
    }

    fun hideDisplayPurchasesTotal() {
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

        val total = products.calculateTotal(DisplayTotal.COMPLETED)
        if (products.preferences.displayMoney && total.isNotEmpty()) {
            shareText += "\n= $total"
        } else {
            if (shareText.isNotEmpty()) {
                shareText = shareText.dropLast(1)
            }
        }

        return Result.success(shareText)
    }

    fun getProductsUpResult(uid: String): Result<Pair<Product, Product>> {
        val formatProducts = products.formatProducts()
        return if (formatProducts.size < 2) {
            Result.failure(Exception())
        } else {
            savedSelectedUid = uid

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

    fun getProductsDownResult(uid: String): Result<Pair<Product, Product>> {
        val formatProducts = products.formatProducts()
        return if (formatProducts.size < 2) {
            Result.failure(Exception())
        } else {
            savedSelectedUid = uid

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

    fun sortProductsResult(sortBy: SortBy): Result<List<Product>> {
        val sortProducts = products.shoppingList.products.sortProducts(sort = Sort(sortBy))
        return if (sortProducts.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = sortProducts.mapIndexed { index, product ->
                product.copy(
                    position = index,
                    lastModified = System.currentTimeMillis()
                )
            }
            Result.success(success)
        }
    }

    fun reverseSortProductsResult(): Result<List<Product>> {
        val sortProducts = products.formatProducts().reversed()
        return if (sortProducts.isEmpty()) {
            Result.failure(Exception())
        } else {
            val success = sortProducts.mapIndexed { index, product ->
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

    private fun toProductNotFoundText(location: ShoppingListLocation): UiText = when(location) {
        ShoppingListLocation.PURCHASES -> UiText.FromResources(R.string.products_text_purchasesProductsNotFound)

        ShoppingListLocation.ARCHIVE -> UiText.FromResources(R.string.products_text_archiveProductsNotFound)

        ShoppingListLocation.TRASH -> UiText.FromResources(R.string.products_text_trashProductsNotFound)
    }
}

data class ProductsScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingListName: UiText = UiText.Nothing,
    val shoppingListLocation: ShoppingListLocation? = null,
    val shoppingListCompleted: Boolean = false,
    val products: List<ProductItem> = listOf(),
    val productsNotFoundText: UiText = UiText.Nothing,
    val showProductsMenu: Boolean = false,
    val totalText: UiText = UiText.Nothing,
    val reminderText: UiText = UiText.Nothing,
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val showSort: Boolean = false,
    val displayTotal: DisplayTotal = DisplayTotal.DefaultValue,
    val showDisplayTotal: Boolean = false,
    val showHiddenProducts: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val displayMoney: Boolean = true,
    val completedWithCheckbox: Boolean = false,
    val selectedUids: List<String>? = null
)