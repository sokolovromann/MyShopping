package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListPreferences
import ru.sokolovromann.myshopping.data.repository.model.ShoppingLists
import ru.sokolovromann.myshopping.ui.utils.getShoppingListItems

class CopyProductState {

    private var product by mutableStateOf(Product())

    var screenData by mutableStateOf(CopyProductScreenData())
        private set

    fun showLoading() {
        screenData = CopyProductScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: ShoppingListPreferences, location: ShoppingListLocation) {
        screenData = CopyProductScreenData(
            screenState = ScreenState.Nothing,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun showShoppingLists(shoppingLists: ShoppingLists, location: ShoppingListLocation) {
        val preferences = shoppingLists.preferences

        screenData = CopyProductScreenData(
            screenState = ScreenState.Showing,
            shoppingLists = shoppingLists.getShoppingListItems(),
            multiColumns = preferences.multiColumns,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun saveProduct(product: Product) {
        this.product = product
    }

    fun selectShoppingList(uid: String) {
        screenData = screenData.copy(shoppingListSelectedUid = uid)
    }

    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }

    fun getProductResult(): Result<Product> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.shoppingListSelectedUid == null) {
            screenData = screenData.copy(screenState = ScreenState.Showing)
            Result.failure(Exception())
        } else {
            val success = Product(
                shoppingUid = screenData.shoppingListSelectedUid!!,
                name = product.name,
                quantity = product.quantity,
                price = product.price,
                discount = product.discount,
                taxRate = product.taxRate,
                completed = product.completed
            )

            Result.success(success)
        }
    }
}

data class CopyProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val shoppingLists: List<ShoppingListItem> = listOf(),
    val shoppingListSelectedUid: String? = null,
    val multiColumns: Boolean = false,
    val location: ShoppingListLocation = ShoppingListLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)