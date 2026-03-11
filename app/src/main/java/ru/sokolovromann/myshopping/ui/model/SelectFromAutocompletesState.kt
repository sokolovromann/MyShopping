package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime

class SelectFromAutocompletesState {

    private var suggestions: Collection<Suggestion> by mutableStateOf(listOf())

    private var shoppingListWithConfig by mutableStateOf(ShoppingListWithConfig())

    var autocompleteNames: List<Pair<String, UID>> by mutableStateOf(listOf())
        private set

    var selectedUids: List<UID> by mutableStateOf(listOf())
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(suggestions: Collection<Suggestion>, shoppingListWithConfig: ShoppingListWithConfig) {
        this.suggestions = suggestions
        this.shoppingListWithConfig = shoppingListWithConfig

        autocompleteNames = UiAutocompletesMapper.toUiNamesWithUids(suggestions).toList()
        selectedUids = listOf()
        deviceSize = shoppingListWithConfig.getDeviceConfig().getDeviceSize()
        multiColumns = deviceSize == DeviceSize.Large
        waiting = false
    }

    fun onSelected(selected: Boolean, uid: UID) {
        selectedUids = selectedUids.toMutableList().apply {
            if (selected) add(uid) else remove(uid)
        }
    }

    fun onAutocompleteSelected(selected: Boolean, uid: UID) {
        selectedUids = selectedUids.toMutableList().apply {
            if (selected) add(uid) else remove(uid)
        }
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return suggestions.isEmpty()
    }

    fun getAddedProducts(): Collection<Pair<Product, Suggestion>> {
        val products: MutableCollection<Pair<Product, Suggestion>> = mutableListOf()
        selectedUids.forEach { uid ->
            suggestions.find { it.uid == uid }?.let {
                if (findProduct(it.name) == null) {
                    val product = Product(
                        shoppingUid = shoppingListWithConfig.getShopping().uid,
                        name = it.name
                    )
                    val newSuggestion = it.copy(
                        lastModified = DateTime.getCurrent(),
                        used = it.used.plus(1)
                    )
                    val productWithSuggestion = Pair(product, newSuggestion)
                    products.add(productWithSuggestion)
                }
            }
        }
        return products
    }

    fun getDeletedProducts(): Collection<Product> {
        val products: MutableCollection<Product> = mutableListOf()
        suggestions.forEach { suggestion ->
            if (!selectedUids.contains(suggestion.uid)) {
                findProduct(suggestion.name)?.let {
                    products.add(it)
                }
            }
        }
        return products
    }

    private fun findProduct(name: String): Product? {
        return shoppingListWithConfig.getSortedProducts().find {
            it.name.equals(name, true)
        }
    }
}