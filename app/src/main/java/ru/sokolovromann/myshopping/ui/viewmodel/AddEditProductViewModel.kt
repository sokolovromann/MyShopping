package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
    private val appConfigRepository: AppConfigRepository,
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

            is AddEditProductEvent.ProductNameFocusChanged -> productNameFocusChanged(event)

            is AddEditProductEvent.ProductUidChanged -> productUidChanged(event)

            is AddEditProductEvent.ProductBrandChanged -> productBrandChanged(event)

            is AddEditProductEvent.ProductSizeChanged -> productSizeChanged(event)

            is AddEditProductEvent.ProductColorChanged -> productColorChanged(event)

            is AddEditProductEvent.ProductManufacturerChanged -> productManufacturerChanged(event)

            is AddEditProductEvent.ProductQuantityChanged -> productQuantityChanged(event)

            is AddEditProductEvent.ProductQuantitySymbolChanged -> productQuantitySymbolChanged(event)

            is AddEditProductEvent.ProductPriceChanged -> productPriceChanged(event)

            is AddEditProductEvent.ProductDiscountChanged -> productDiscountChanged(event)

            AddEditProductEvent.ProductDiscountAsPercentSelected -> productDiscountAsPercentSelected()

            AddEditProductEvent.ProductDiscountAsMoneySelected -> productDiscountAsMoneySelected()

            is AddEditProductEvent.ProductTotalChanged -> productTotalChanged(event)

            is AddEditProductEvent.LockProductElementSelected -> lockProductElementSelected(event)

            is AddEditProductEvent.AutocompleteNameSelected -> autocompleteNameSelected(event)

            is AddEditProductEvent.AutocompleteBrandSelected -> autocompleteBrandSelected(event)

            is AddEditProductEvent.AutocompleteSizeSelected -> autocompleteSizeSelected(event)

            is AddEditProductEvent.AutocompleteColorSelected -> autocompleteColorSelected(event)

            is AddEditProductEvent.AutocompleteManufacturerSelected -> autocompleteManufacturerSelected(event)

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

            AddEditProductEvent.InvertNameOtherFields -> invertNameOtherFields()

            AddEditProductEvent.InvertPriceOtherFields -> invertPriceOtherFields()

            AddEditProductEvent.HideProductDiscountAsPercentMenu -> hideProductDiscountAsPercentMenu()

            AddEditProductEvent.HideLockProductElement -> hideLockProductElement()
        }
    }

    private fun getAddEditProduct() = viewModelScope.launch {
        shoppingListsRepository.getAddEditProduct(shoppingUid, productUid).firstOrNull()?.let {
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
            getAutocompletes(addEditProduct.getSearchName())
        }
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch {
        val search = name.trim()
        val autocompletes = autocompletesRepository.searchAutocompletesLikeName(
            search = search,
            language = Locale.getDefault().language
        ).firstOrNull() ?: Autocompletes()
        autocompletesLoaded(autocompletes)
    }

    private suspend fun autocompletesLoaded(
        autocompletes: Autocompletes
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        val names = autocompletes.getNames(search = currentName)
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

        if (productUid == null || addEditProductState.productNameFocus) {
            addEditProductState.showAutocompleteElements(
                brands = autocompletes.getBrands(),
                sizes = autocompletes.getSizes(),
                colors = autocompletes.getColors(),
                manufacturers = autocompletes.getManufacturers(),
                quantities = autocompletes.getQuantities(),
                quantitySymbols = autocompletes.getQuantitySymbols(),
                prices = autocompletes.getPrices(),
                discounts = autocompletes.getDiscounts(),
                totals = autocompletes.getTotals()
            )
        } else {
            addEditProductState.showAutocompleteElementsIf(
                brands = autocompletes.getBrands(),
                sizes = autocompletes.getSizes(),
                colors = autocompletes.getColors(),
                manufacturers = autocompletes.getManufacturers(),
                quantities = autocompletes.getQuantities(),
                quantitySymbols = autocompletes.getQuantitySymbols(),
                prices = autocompletes.getPrices(),
                discounts = autocompletes.getDiscounts(),
                totals = autocompletes.getTotals()
            )
        }
    }

    private fun saveProduct() = viewModelScope.launch {
        val product = addEditProductState.getProductResult(productUid == null)
            .getOrElse { return@launch }

        val productUidExists = shoppingListsRepository.checkIfProductExists(product.productUid).first()
        if (productUidExists != null && product.productUid != productUid) {
            addEditProductState.showUidError()
            return@launch
        }

        shoppingListsRepository.saveProduct(product)

        addEditProductState.getAutocompleteResult()
            .onSuccess { autocompletesRepository.saveAutocomplete(it) }

        addEditProductState.getProductLockResult().onSuccess {
            appConfigRepository.lockProductElement(it)
        }

        withContext(dispatchers.main) {
            val event = AddEditProductScreenEvent.ShowBackScreenAndUpdateProductsWidget(shoppingUid)
            _screenEventFlow.emit(event)
        }
    }

    private fun cancelSavingProduct() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
    }

    private fun productBrandChanged(event: AddEditProductEvent.ProductBrandChanged) {
        addEditProductState.changeBrandValue(event.value)
    }

    private fun productSizeChanged(event: AddEditProductEvent.ProductSizeChanged) {
        addEditProductState.changeSizeValue(event.value)
    }

    private fun productColorChanged(event: AddEditProductEvent.ProductColorChanged) {
        addEditProductState.changeColorValue(event.value)
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

    private fun productNameFocusChanged(event: AddEditProductEvent.ProductNameFocusChanged) {
        addEditProductState.changeNameFocus(event.focused)
    }

    private fun productUidChanged(event: AddEditProductEvent.ProductUidChanged) {
        addEditProductState.changeUidValue(event.value)
    }

    private fun productManufacturerChanged(event: AddEditProductEvent.ProductManufacturerChanged) {
        addEditProductState.changeManufacturerValue(event.value)
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

    private fun autocompleteBrandSelected(event: AddEditProductEvent.AutocompleteBrandSelected) {
        addEditProductState.selectAutocompleteBrand(event.brand)
    }

    private fun autocompleteSizeSelected(event: AddEditProductEvent.AutocompleteSizeSelected) {
        addEditProductState.selectAutocompleteSize(event.size)
    }

    private fun autocompleteColorSelected(event: AddEditProductEvent.AutocompleteColorSelected) {
        addEditProductState.selectAutocompleteColor(event.color)
    }

    private fun autocompleteManufacturerSelected(event: AddEditProductEvent.AutocompleteManufacturerSelected) {
        addEditProductState.selectAutocompleteManufacturer(event.manufacturer)
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

    private fun invertNameOtherFields() {
        addEditProductState.invertNameOtherFields()
    }

    private fun invertPriceOtherFields() {
        addEditProductState.invertPriceOtherFields()
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