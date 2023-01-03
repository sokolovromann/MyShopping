package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AddEditProductRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val repository: AddEditProductRepository,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditProductEvent> {

    val addEditProductState: AddEditProductState = AddEditProductState()

    private val _screenEventFlow: MutableSharedFlow<AddEditProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditProductScreenEvent> = _screenEventFlow

    private val productUid: String? = savedStateHandle.get<String>(UiRouteKey.ProductUid.key)

    init {
        getAddEditProduct()
    }

    override fun onEvent(event: AddEditProductEvent) {
        when (event) {
            AddEditProductEvent.SaveProduct -> saveProduct()

            AddEditProductEvent.CancelSavingProduct -> cancelSavingProduct()

            is AddEditProductEvent.ProductNameChanged -> productNameChanged(event)

            is AddEditProductEvent.ProductQuantityChanged -> productQuantityChanged(event)

            is AddEditProductEvent.ProductQuantitySymbolChanged -> productQuantitySymbolChanged(event)

            is AddEditProductEvent.ProductPriceChanged -> productPriceChanged(event)

            is AddEditProductEvent.ProductDiscountChanged -> productDiscountChanged(event)

            AddEditProductEvent.ProductDiscountAsPercentSelected -> productDiscountAsPercentSelected()

            AddEditProductEvent.ProductDiscountAsMoneySelected -> productDiscountAsMoneySelected()

            is AddEditProductEvent.AutocompleteNameSelected -> autocompleteNameSelected(event)

            is AddEditProductEvent.AutocompleteQuantitySelected -> autocompleteQuantitySelected(event)

            is AddEditProductEvent.AutocompleteQuantitySymbolSelected -> autocompleteQuantitySymbolSelected(event)

            is AddEditProductEvent.AutocompletePriceSelected -> autocompletePriceSelected(event)

            is AddEditProductEvent.AutocompleteDiscountSelected -> autocompleteDiscountSelected(event)

            AddEditProductEvent.AutocompleteMinusOneQuantitySelected -> autocompleteMinusOneQuantitySelected()

            AddEditProductEvent.AutocompletePlusOneQuantitySelected -> autocompletePlusOneQuantitySelected()

            AddEditProductEvent.ShowProductDiscountAsPercentMenu -> showProductDiscountAsPercentMenu()

            AddEditProductEvent.HideProductDiscountAsPercentMenu -> hideProductDiscountAsPercentMenu()

            else -> {}
        }
    }

    private fun getAddEditProduct() = viewModelScope.launch {
        repository.getAddEditProduct(productUid).firstOrNull()?.let {
            addEditProductLoaded(it)
        }
    }

    private suspend fun addEditProductLoaded(
        addEditProduct: AddEditProduct
    ) = withContext(dispatchers.main) {
        val shoppingUid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)
        val product = Product(shoppingUid = shoppingUid ?: "")

        if (productUid == null) {
            addEditProductState.populate(addEditProduct.copy(product = product))
            showKeyboard()
        } else {
            addEditProductState.populate(addEditProduct)
            getAutocompletes(product.name)
        }
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch {
        val search = name.trim()
        val addEditProductAutocomplete = repository.getAutocompletes(search).firstOrNull()
            ?: AddEditProductAutocomplete()
        addEditProductAutocompleteLoaded(addEditProductAutocomplete)
    }

    private suspend fun addEditProductAutocompleteLoaded(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val names = addEditProductAutocomplete.names()
        if (names.isEmpty()) {
            addEditProductState.hideAutocompletes()
            return@withContext
        }

        val currentName = addEditProductState.screenData.nameValue.text
        val containsName = names.contains(currentName)

        when (addEditProductAutocomplete.preferences.displayAutocomplete) {
            DisplayAutocomplete.ALL -> {
                if (containsName) {
                    addEditProductState.showAutocompletes(
                        names = listOf(),
                        quantities = addEditProductAutocomplete.quantities(currentName),
                        quantitySymbols = addEditProductAutocomplete.quantitySymbols(currentName),
                        prices = addEditProductAutocomplete.prices(currentName),
                        discounts = addEditProductAutocomplete.discounts(currentName)
                    )
                } else {
                    addEditProductState.showAutocompletes(
                        names = names,
                        quantities = addEditProductAutocomplete.quantities(currentName),
                        quantitySymbols = addEditProductAutocomplete.quantitySymbols(currentName),
                        prices = addEditProductAutocomplete.prices(currentName),
                        discounts = addEditProductAutocomplete.discounts(currentName)
                    )
                }
            }

            DisplayAutocomplete.NAME -> {
                if (containsName) {
                    addEditProductState.hideAutocompleteNames()
                } else {
                    addEditProductState.showAutocompleteNames(addEditProductAutocomplete.names())
                }
            }

            DisplayAutocomplete.HIDE -> {
                addEditProductState.hideAutocompletes()
            }
        }
    }

    private fun saveProduct() = viewModelScope.launch {
        val product = addEditProductState.getProductResult()
            .getOrElse { return@launch }

        if (productUid == null) {
            repository.addProduct(product)
        } else {
            repository.editProduct(product)
        }

        addEditProductState.getAutocompleteResult()
            .onSuccess { repository.addAutocomplete(it) }

        withContext(dispatchers.main) {
            _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
        }
    }

    private fun cancelSavingProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
    }

    private fun productNameChanged(event: AddEditProductEvent.ProductNameChanged) {
        addEditProductState.changeNameValue(event.value)

        val minLength = 2
        val displayAutocomplete = addEditProductState.getDisplayAutocomplete()
        val hideAutocomplete = displayAutocomplete == DisplayAutocomplete.HIDE ||
                event.value.text.length < minLength

        if (hideAutocomplete) {
            addEditProductState.hideAutocompletes()
        } else {
            getAutocompletes(event.value.text)
        }
    }

    private fun productQuantityChanged(event: AddEditProductEvent.ProductQuantityChanged) {
        addEditProductState.changeQuantityValue(event.value)
    }

    private fun productQuantitySymbolChanged(event: AddEditProductEvent.ProductQuantitySymbolChanged) {
        addEditProductState.changeQuantitySymbolValue(event.value)
    }

    private fun productPriceChanged(event: AddEditProductEvent.ProductPriceChanged) {
        addEditProductState.changePriceValue(event.value)
    }

    private fun productDiscountChanged(event: AddEditProductEvent.ProductDiscountChanged) {
        addEditProductState.changeDiscountValue(event.value)
    }

    private fun productDiscountAsPercentSelected() {
        addEditProductState.selectDiscountAsPercent()
    }

    private fun productDiscountAsMoneySelected() {
        addEditProductState.selectDiscountAsMoney()
    }

    private fun autocompleteNameSelected(event: AddEditProductEvent.AutocompleteNameSelected) {
        addEditProductState.selectAutocompleteName(event.text)
    }

    private fun autocompleteQuantitySelected(event: AddEditProductEvent.AutocompleteQuantitySelected) {
        addEditProductState.selectAutocompleteQuantity(event.quantity)
    }

    private fun autocompleteQuantitySymbolSelected(event: AddEditProductEvent.AutocompleteQuantitySymbolSelected) {
        addEditProductState.selectAutocompleteQuantitySymbol(event.quantity)
    }

    private fun autocompletePriceSelected(event: AddEditProductEvent.AutocompletePriceSelected) {
        addEditProductState.selectAutocompletePrice(event.price)
    }

    private fun autocompleteDiscountSelected(event: AddEditProductEvent.AutocompleteDiscountSelected) {
        addEditProductState.selectAutocompleteDiscount(event.discount)
    }

    private fun autocompleteMinusOneQuantitySelected() {
        addEditProductState.minusOneQuantity()
    }

    private fun autocompletePlusOneQuantitySelected() {
        addEditProductState.plusOneQuantity()
    }

    private fun showProductDiscountAsPercentMenu() {
        addEditProductState.showDiscountAsPercent()
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowKeyboard)
    }

    private fun hideProductDiscountAsPercentMenu() {
        addEditProductState.hideDiscountAsPercent()
    }
}