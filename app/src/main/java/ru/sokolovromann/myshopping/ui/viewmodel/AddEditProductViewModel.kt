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
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ProductWithConfig
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.utils.asSearchQuery
import ru.sokolovromann.myshopping.data.utils.sortedAutocompletes
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
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
        shoppingListsRepository.getProductWithConfig(productUid).firstOrNull()?.let {
            productLoaded(it)
        }
    }

    private suspend fun productLoaded(
        productWithConfig: ProductWithConfig
    ) = withContext(dispatchers.main) {
        if (productUid == null) {
            val newProduct = Product(shoppingUid = shoppingUid)
            val newProductWithConfig = productWithConfig.copy(product = newProduct)
            addEditProductState.populate(newProductWithConfig)
            showKeyboard()
        } else {
            addEditProductState.populate(productWithConfig)

            val searchName = productWithConfig.product.name.trim()
            getAutocompletes(searchName)
        }
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch {
        val search = name.trim()
        val autocompletes = autocompletesRepository.searchAutocompletesLikeName(
            search = search
        ).firstOrNull() ?: listOf()
        autocompletesLoaded(autocompletes)
    }

    private suspend fun autocompletesLoaded(
        autocompletes: List<Autocomplete>
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        val names = searchAutocompletesLikeName(autocompletes, currentName)
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

        val filterByPersonal = filterAutocompletesByPersonal(autocompletes).sortedAutocompletes(
            Sort(SortBy.LAST_MODIFIED, false)
        )

        if (productUid == null || addEditProductState.productNameFocus) {
            addEditProductState.showAutocompleteElements(
                brands = filterAutocompleteBrands(filterByPersonal),
                sizes = filterAutocompleteSizes(filterByPersonal),
                colors = filterAutocompleteColors(filterByPersonal),
                manufacturers = filterAutocompletesManufacturers(filterByPersonal),
                quantities = filterAutocompletesQuantities(filterByPersonal),
                quantitySymbols = filterAutocompletesQuantitySymbols(filterByPersonal),
                prices = filterAutocompletesPrices(filterByPersonal),
                discounts = filterAutocompletesDiscounts(filterByPersonal),
                totals = filterAutocompletesTotals(filterByPersonal)
            )
        } else {
            addEditProductState.showAutocompleteElementsIf(
                brands = filterAutocompleteBrands(filterByPersonal),
                sizes = filterAutocompleteSizes(filterByPersonal),
                colors = filterAutocompleteColors(filterByPersonal),
                manufacturers = filterAutocompletesManufacturers(filterByPersonal),
                quantities = filterAutocompletesQuantities(filterByPersonal),
                quantitySymbols = filterAutocompletesQuantitySymbols(filterByPersonal),
                prices = filterAutocompletesPrices(filterByPersonal),
                discounts = filterAutocompletesDiscounts(filterByPersonal),
                totals = filterAutocompletesTotals(filterByPersonal)
            )
        }
    }

    private fun saveProduct() = viewModelScope.launch {
        shoppingListsRepository.saveProduct(addEditProductState.getCurrentProduct())
            .onSuccess {
                autocompletesRepository.saveAutocomplete(addEditProductState.getAutocomplete())
                appConfigRepository.lockProductElement(addEditProductState.screenData.lockProductElement)

                withContext(dispatchers.main) {
                    val event = AddEditProductScreenEvent.ShowBackScreenAndUpdateProductsWidget(shoppingUid)
                    _screenEventFlow.emit(event)
                }
            }
            .onFailure {
                when (it) {
                    is InvalidNameException -> { addEditProductState.showNameError() }
                    is InvalidUidException -> { addEditProductState.showUidError() }
                    else -> {}
                }
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

    private fun filterAutocompletesByPersonal(autocompletes: List<Autocomplete>): List<Autocomplete> {
        return if (addEditProductState.getUserPreferences().displayDefaultAutocompletes) {
            autocompletes
        } else {
            autocompletes.filter { it.personal }
        }
    }

    private fun searchAutocompletesLikeName(
        autocompletes: List<Autocomplete>,
        search: String
    ): List<Autocomplete>  {
        val endIndex = search.length - 1
        val partition = filterAutocompletesByPersonal(autocompletes)
            .partition {
                val charsName = it.name.asSearchQuery().toCharArray(endIndex = endIndex)
                val charsSearch = search.asSearchQuery().toCharArray(endIndex = endIndex)
                charsName.contentEquals(charsSearch)
            }
        val searchAutocompletes = partition.first
            .sortedAutocompletes()
            .distinctBy { it.name.lowercase() }

        val otherAutocompletes = partition.second
            .sortedAutocompletes()
            .distinctBy { it.name.lowercase() }

        val bothAutocompletes = mutableListOf<Autocomplete>()
        return bothAutocompletes
            .apply {
                addAll(searchAutocompletes)
                addAll(otherAutocompletes)
            }
            .filterIndexed { index, autocomplete ->
                autocomplete.name.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesNames
            }
    }

    private fun filterAutocompleteBrands(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.brand }
            .distinct()
            .filterIndexed { index, brand ->
                brand.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteSizes(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.size }
            .distinct()
            .filterIndexed { index, size ->
                size.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteColors(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.color }
            .distinct()
            .filterIndexed { index, color ->
                color.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompletesManufacturers(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.manufacturer }
            .distinct()
            .filterIndexed { index, manufacturer ->
                manufacturer.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompletesQuantities(autocompletes: List<Autocomplete>): List<Quantity> {
        return autocompletes
            .map { it.quantity }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompletesQuantitySymbols(autocompletes: List<Autocomplete>): List<Quantity> {
        return autocompletes
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() &&
                        index <= addEditProductState.getUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompletesPrices(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.price }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompletesDiscounts(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.discount }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompletesTotals(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.total }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index <= addEditProductState.getUserPreferences().maxAutocompletesMoneys
            }
    }
}