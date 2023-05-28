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
import java.util.Locale
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

    private val shoppingUid: String = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key) ?: ""

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

            is AddEditProductEvent.ProductTotalChanged -> productTotalChanged(event)

            is AddEditProductEvent.LockProductElementSelected -> lockProductElementSelected(event)

            is AddEditProductEvent.AutocompleteNameSelected -> autocompleteNameSelected(event)

            is AddEditProductEvent.AutocompleteQuantitySelected -> autocompleteQuantitySelected(event)

            is AddEditProductEvent.AutocompleteQuantitySymbolSelected -> autocompleteQuantitySymbolSelected(event)

            is AddEditProductEvent.AutocompletePriceSelected -> autocompletePriceSelected(event)

            is AddEditProductEvent.AutocompleteTotalSelected -> autocompleteTotalSelected(event)

            is AddEditProductEvent.AutocompleteDiscountSelected -> autocompleteDiscountSelected(event)

            AddEditProductEvent.AutocompleteMinusOneQuantitySelected -> autocompleteMinusOneQuantitySelected()

            AddEditProductEvent.AutocompletePlusOneQuantitySelected -> autocompletePlusOneQuantitySelected()

            is AddEditProductEvent.ProductNoteChanged -> productNoteChanged(event)

            AddEditProductEvent.SelectLockProductElement -> selectLockProductElement()

            AddEditProductEvent.ShowProductDiscountAsPercentMenu -> showProductDiscountAsPercentMenu()

            AddEditProductEvent.HideProductDiscountAsPercentMenu -> hideProductDiscountAsPercentMenu()

            AddEditProductEvent.HideLockProductElement -> hideLockProductElement()
        }
    }

    private fun getAddEditProduct() = viewModelScope.launch {
        repository.getAddEditProduct(shoppingUid, productUid).firstOrNull()?.let {
            addEditProductLoaded(it)
        }
    }

    private suspend fun addEditProductLoaded(
        addEditProduct: AddEditProduct
    ) = withContext(dispatchers.main) {
        if (productUid == null) {
            val product = Product(shoppingUid = shoppingUid)
            addEditProductState.populate(addEditProduct.copy(product = product))
            showKeyboard()
        } else {
            addEditProductState.populate(addEditProduct)
            getAutocompletes(addEditProduct.formatName())
        }
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch {
        val search = name.trim()
        val autocompletes = repository.getAutocompletes(
            search = search,
            language = Locale.getDefault().language
        ).firstOrNull() ?: Autocompletes()
        autocompletesLoaded(autocompletes)
    }

    private suspend fun autocompletesLoaded(
        autocompletes: Autocompletes
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        val names = autocompletes.names(search = currentName)
        if (names.isEmpty()) {
            addEditProductState.hideAutocompletes()
            return@withContext
        }

        val containsAutocomplete = names.find { it.name.lowercase() == currentName.lowercase() }

        if (containsAutocomplete == null) {
            addEditProductState.showAutocompleteNames(names)
        } else {
            addEditProductState.hideAutocompleteNames(containsAutocomplete)
        }

        addEditProductState.showAutocompleteElements(
            quantities = autocompletes.quantities(),
            quantitySymbols = autocompletes.quantitySymbols(),
            prices = autocompletes.prices(),
            discounts = autocompletes.discounts(),
            totals = autocompletes.totals()
        )
    }

    private fun saveProduct() = viewModelScope.launch {
        val product = addEditProductState.getProductResult(productUid == null)
            .getOrElse { return@launch }

        if (productUid == null) {
            repository.addProduct(product)
        } else {
            repository.editProduct(product)
        }

        addEditProductState.getAutocompleteResult()
            .onSuccess { repository.addAutocomplete(it) }

        addEditProductState.getProductLockResult().onSuccess {
            when (it) {
                LockProductElement.QUANTITY -> repository.lockProductQuantity()
                LockProductElement.PRICE -> repository.lockProductPrice()
                LockProductElement.TOTAL -> repository.lockProductTotal()
            }
        }

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
        if (event.value.text.length >= minLength) {
            val search = event.value.text
            getAutocompletes(search)
        } else {
            addEditProductState.hideAutocompletes()
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

    private fun productTotalChanged(event: AddEditProductEvent.ProductTotalChanged) {
        addEditProductState.changeProductTotalValue(event.value)
    }

    private fun lockProductElementSelected(event: AddEditProductEvent.LockProductElementSelected) {
        addEditProductState.lockProductElementSelected(event.lockProductElement)
    }

    private fun autocompleteNameSelected(event: AddEditProductEvent.AutocompleteNameSelected) {
        addEditProductState.selectAutocompleteName(event.autocomplete)
        getAutocompletes(event.autocomplete.name)
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

    private fun autocompleteTotalSelected(event: AddEditProductEvent.AutocompleteTotalSelected) {
        addEditProductState.selectAutocompleteTotal(event.total)
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

    private fun productNoteChanged(event: AddEditProductEvent.ProductNoteChanged) {
        addEditProductState.changeProductNoteValue(event.value)
    }

    private fun showProductDiscountAsPercentMenu() {
        addEditProductState.showDiscountAsPercent()
    }

    private fun selectLockProductElement() {
        addEditProductState.selectLockProductElement()
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowKeyboard)
    }

    private fun hideProductDiscountAsPercentMenu() {
        addEditProductState.hideDiscountAsPercent()
    }

    private fun hideLockProductElement() {
        addEditProductState.hideLockProductElement()
    }
}