package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.utils.uppercaseFirst
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper

class SelectFromAutocompletesState {

    private var autocompletesWithConfig by mutableStateOf(AutocompletesWithConfig())

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var autocompleteNames: List<UiString> by mutableStateOf(listOf())
        private set

    var selectedNames: List<String>? by mutableStateOf(null)
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(autocompletesWithConfig: AutocompletesWithConfig) {
        this.autocompletesWithConfig = autocompletesWithConfig

        autocompleteNames = UiAutocompletesMapper.toAutocompleteUiNames(autocompletesWithConfig)
        selectedNames = null
        deviceSize = autocompletesWithConfig.appConfig.deviceConfig.getDeviceSize()
        multiColumns = deviceSize == DeviceSize.Large
        waiting = false
    }

    fun saveShoppingList(shoppingListWithConfig: ShoppingListWithConfig) {
        this.shoppingListWithConfig = shoppingListWithConfig
    }

    fun onAutocompleteSelected(selected: Boolean, name: String) {
        val names = (selectedNames?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(name) else remove(name)
        }
        selectedNames = if (names.isEmpty()) null else names
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return autocompletesWithConfig.isEmpty()
    }

    fun getAddedProducts(): List<Product> {
        val products: MutableList<Product> = mutableListOf()
        selectedNames?.forEach { name ->
            if (getProducts(name).isEmpty()) {
                val product = Product(
                    shoppingUid = shoppingListWithConfig.getShopping().uid,
                    name = name
                )
                products.add(product)
            }
        }
        return products
    }

    fun getDeletedProducts(): List<Product> {
        val products: MutableList<Product> = mutableListOf()
        autocompletesWithConfig.getNames().forEach { name ->
            if (!selectedNames?.contains(name)!!) {
                getProducts(name).forEach { products.add(it) }
            }
        }
        return products
    }

    private fun getProducts(name: String): List<Product> {
        return shoppingListWithConfig.getSortedProducts()
            .filter { it.name.uppercaseFirst() == name }
    }
}