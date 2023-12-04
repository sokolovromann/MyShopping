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
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.LockProductElement
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
import ru.sokolovromann.myshopping.ui.model.AddEditProductState
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
    private val appConfigRepository: AppConfigRepository,
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
    ) = withContext(AppDispatchers.Main) {
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
    ) = withContext(AppDispatchers.Main) {
        val currentName = addEditProductState.nameValue.text
        val names = searchAutocompletesLikeName(autocompletes, currentName)
        if (names.isEmpty()) {
            addEditProductState.onHideAutocompletes()
            return@withContext
        }

        val containsAutocomplete = names.find { it.name.asSearchQuery() == currentName.asSearchQuery() }
        val filterByPersonal = filterAutocompletesByPersonal(autocompletes).sortedAutocompletes(
            Sort(SortBy.LAST_MODIFIED, false)
        )

        val quantitySymbols = if (addEditProductState.quantitySymbolValue.isEmpty()) filterAutocompletesQuantitySymbols(filterByPersonal)  else listOf()
        val autocompletesSelectedValue = addEditProductState.autocompletes.copy(
            names = names,
            brands = if (addEditProductState.brandValue.isEmpty()) filterAutocompleteBrands(filterByPersonal) else listOf(),
            sizes = if (addEditProductState.sizeValue.isEmpty()) filterAutocompleteSizes(filterByPersonal) else listOf(),
            colors = if (addEditProductState.colorValue.isEmpty()) filterAutocompleteColors(filterByPersonal) else listOf(),
            manufacturers = if (addEditProductState.manufacturerValue.isEmpty()) filterAutocompletesManufacturers(filterByPersonal) else listOf(),
            quantities = if (addEditProductState.quantityValue.isEmpty()) filterAutocompletesQuantities(filterByPersonal) else listOf(),
            quantitySymbols = quantitySymbols,
            displayDefaultQuantitySymbols = quantitySymbols.isEmpty() && addEditProductState.quantitySymbolValue.isEmpty(),
            prices = if (addEditProductState.priceValue.isEmpty()) filterAutocompletesPrices(filterByPersonal) else listOf(),
            discounts = if (addEditProductState.discountValue.isEmpty()) filterAutocompletesDiscounts(filterByPersonal) else listOf(),
            totals = if (addEditProductState.totalValue.isEmpty()) filterAutocompletesTotals(filterByPersonal) else listOf(),
            selected = containsAutocomplete
        )
        addEditProductState.onShowAutocomplete(autocompletesSelectedValue)

        if (containsAutocomplete != null) {
            addEditProductState.onNameSelected(containsAutocomplete)
        }
    }

    private fun saveProduct() = viewModelScope.launch {
        addEditProductState.onWaiting()

        shoppingListsRepository.saveProduct(addEditProductState.getCurrentProduct())
            .onSuccess {
                if (addEditProductState.getCurrentUserPreferences().saveProductToAutocompletes) {
                    autocompletesRepository.saveAutocomplete(addEditProductState.getCurrentAutocomplete())
                }
                appConfigRepository.lockProductElement(addEditProductState.lockProductElementValue.selected)

                withContext(AppDispatchers.Main) {
                    val event = AddEditProductScreenEvent.ShowBackScreenAndUpdateProductsWidget(shoppingUid)
                    _screenEventFlow.emit(event)
                }
            }
            .onFailure {
                when (it) {
                    is InvalidNameException -> { addEditProductState.onInvalidNameValue() }
                    is InvalidUidException -> { addEditProductState.onInvalidUidValue() }
                    else -> {}
                }
            }
    }

    private fun cancelSavingProduct() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
    }

    private fun productBrandChanged(event: AddEditProductEvent.ProductBrandChanged) {
        addEditProductState.onBrandValueChanged(event.value)
    }

    private fun productSizeChanged(event: AddEditProductEvent.ProductSizeChanged) {
        addEditProductState.onSizeValueChanged(event.value)
    }

    private fun productColorChanged(event: AddEditProductEvent.ProductColorChanged) {
        addEditProductState.onColorValueChanged(event.value)
    }

    private fun productNameChanged(event: AddEditProductEvent.ProductNameChanged) {
        addEditProductState.onNameValueChanged(event.value)

        val minLength = 2
        if (event.value.text.length >= minLength) {
            val search = event.value.text
            getAutocompletes(search)
        } else {
            addEditProductState.onHideAutocompletes()
        }
    }

    private fun productUidChanged(event: AddEditProductEvent.ProductUidChanged) {
        addEditProductState.onUidValueChanged(event.value)
    }

    private fun productManufacturerChanged(event: AddEditProductEvent.ProductManufacturerChanged) {
        addEditProductState.onManufacturerValueChanged(event.value)
    }

    private fun productQuantityChanged(event: AddEditProductEvent.ProductQuantityChanged) {
        addEditProductState.onQuantityValueChanged(event.value)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.PRICE -> calculatePrice()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun productQuantitySymbolChanged(event: AddEditProductEvent.ProductQuantitySymbolChanged) {
        addEditProductState.onQuantitySymbolValueChanged(event.value)
    }

    private fun productPriceChanged(event: AddEditProductEvent.ProductPriceChanged) {
        addEditProductState.onPriceValueChanged(event.value)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> calculateQuantity()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun productDiscountChanged(event: AddEditProductEvent.ProductDiscountChanged) {
        addEditProductState.onDiscountValueChanged(event.value)

        if (addEditProductState.lockProductElementValue.selected == LockProductElement.TOTAL) {
            calculateTotal()
        }
    }

    private fun productDiscountAsPercentSelected() {
        addEditProductState.onDiscountAsPercentSelected(asPercent = true)

        if (addEditProductState.lockProductElementValue.selected == LockProductElement.TOTAL) {
            calculateTotal()
        }
    }

    private fun productDiscountAsMoneySelected() {
        addEditProductState.onDiscountAsPercentSelected(asPercent = false)

        if (addEditProductState.lockProductElementValue.selected == LockProductElement.TOTAL) {
            calculateTotal()
        }
    }

    private fun productTotalChanged(event: AddEditProductEvent.ProductTotalChanged) {
        addEditProductState.onTotalValueChanged(event.value)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> calculateQuantity()
            LockProductElement.PRICE -> calculatePrice()
            LockProductElement.TOTAL -> {}
        }
    }

    private fun lockProductElementSelected(event: AddEditProductEvent.LockProductElementSelected) {
        addEditProductState.onLockProductElementSelected(event.lockProductElement)
    }

    private fun autocompleteNameSelected(event: AddEditProductEvent.AutocompleteNameSelected) {
        addEditProductState.onNameSelected(event.autocomplete)
        getAutocompletes(event.autocomplete.name)
    }

    private fun autocompleteBrandSelected(event: AddEditProductEvent.AutocompleteBrandSelected) {
        addEditProductState.onBrandSelected(event.brand)
    }

    private fun autocompleteSizeSelected(event: AddEditProductEvent.AutocompleteSizeSelected) {
        addEditProductState.onSizeSelected(event.size)
    }

    private fun autocompleteColorSelected(event: AddEditProductEvent.AutocompleteColorSelected) {
        addEditProductState.onColorSelected(event.color)
    }

    private fun autocompleteManufacturerSelected(event: AddEditProductEvent.AutocompleteManufacturerSelected) {
        addEditProductState.onManufacturerSelected(event.manufacturer)
    }

    private fun autocompleteQuantitySelected(event: AddEditProductEvent.AutocompleteQuantitySelected) {
        addEditProductState.onQuantitySelected(event.quantity)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.PRICE -> calculatePrice()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun autocompleteQuantitySymbolSelected(event: AddEditProductEvent.AutocompleteQuantitySymbolSelected) {
        addEditProductState.onQuantitySymbolSelected(event.quantity)
    }

    private fun autocompletePriceSelected(event: AddEditProductEvent.AutocompletePriceSelected) {
        addEditProductState.onPriceSelected(event.price)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> calculateQuantity()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun autocompleteTotalSelected(event: AddEditProductEvent.AutocompleteTotalSelected) {
        addEditProductState.onTotalSelected(event.total)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> calculateQuantity()
            LockProductElement.PRICE -> calculatePrice()
            else -> {}
        }
    }

    private fun autocompleteDiscountSelected(event: AddEditProductEvent.AutocompleteDiscountSelected) {
        addEditProductState.onDiscountSelected(event.discount)

        if (addEditProductState.lockProductElementValue.selected == LockProductElement.TOTAL) {
            calculateTotal()
        }
    }

    private fun autocompleteMinusOneQuantitySelected() {
        val value = addEditProductState.quantityValue.toFloatOrZero().minus(1)
        val quantity = Quantity(value = value)
        val quantityValue = if (quantity.isEmpty()) {
            "".toTextFieldValue()
        } else {
            quantity.toTextFieldValue()
        }
        addEditProductState.onQuantityValueChanged(quantityValue)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.PRICE -> calculatePrice()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun autocompletePlusOneQuantitySelected() {
        val value = addEditProductState.quantityValue.toFloatOrZero().plus(1)
        val quantityValue = Quantity(value = value).toTextFieldValue()
        addEditProductState.onQuantityValueChanged(quantityValue)

        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.PRICE -> calculatePrice()
            LockProductElement.TOTAL -> calculateTotal()
            else -> {}
        }
    }

    private fun productNoteChanged(event: AddEditProductEvent.ProductNoteChanged) {
        addEditProductState.onNoteValueChanged(event.value)
    }

    private fun showProductDiscountAsPercentMenu() {
        addEditProductState.onSelectDiscountAsPercent(expanded = true)
    }

    private fun invertNameOtherFields() {
        addEditProductState.onInvertNameOtherFields()
    }

    private fun invertPriceOtherFields() {
        addEditProductState.onInvertPriceOtherFields()
    }

    private fun selectLockProductElement() {
        addEditProductState.onSelectLockProductElement(expanded = true)
    }

    private fun showKeyboard() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowKeyboard)
    }

    private fun hideProductDiscountAsPercentMenu() {
        addEditProductState.onSelectDiscountAsPercent(expanded = false)
    }

    private fun hideLockProductElement() {
        addEditProductState.onSelectLockProductElement(expanded = false)
    }

    private fun filterAutocompletesByPersonal(autocompletes: List<Autocomplete>): List<Autocomplete> {
        return if (addEditProductState.getCurrentUserPreferences().displayDefaultAutocompletes) {
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
                autocomplete.name.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesNames
            }
    }

    private fun filterAutocompleteBrands(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.brand }
            .distinct()
            .filterIndexed { index, brand ->
                brand.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteSizes(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.size }
            .distinct()
            .filterIndexed { index, size ->
                size.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteColors(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.color }
            .distinct()
            .filterIndexed { index, color ->
                color.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompletesManufacturers(autocompletes: List<Autocomplete>): List<String> {
        return autocompletes
            .map { it.manufacturer }
            .distinct()
            .filterIndexed { index, manufacturer ->
                manufacturer.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompletesQuantities(autocompletes: List<Autocomplete>): List<Quantity> {
        return autocompletes
            .map { it.quantity }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompletesQuantitySymbols(autocompletes: List<Autocomplete>): List<Quantity> {
        return autocompletes
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() &&
                        index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompletesPrices(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.price }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompletesDiscounts(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.discount }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompletesTotals(autocompletes: List<Autocomplete>): List<Money> {
        return autocompletes
            .map { it.total }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun calculateQuantity() {
        val price = addEditProductState.priceValue.toFloatOrZero()
        val total = addEditProductState.totalValue.toFloatOrZero()
        val fieldValue = if (price <= 0f || total <= 0f) {
            "".toTextFieldValue()
        } else {
            Quantity(
                value = total / price,
                decimalFormat = addEditProductState.getCurrentUserPreferences().quantityDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onQuantityValueChanged(fieldValue)
    }

    private fun calculatePrice() {
        val quantity = addEditProductState.quantityValue.toFloatOrZero()
        val total = addEditProductState.totalValue.toFloatOrZero()
        val fieldValue = if (quantity <= 0f || total < 0f) {
            "".toTextFieldValue()
        } else {
            Money(
                value = total / quantity,
                currency = addEditProductState.getCurrentUserPreferences().currency,
                asPercent = false,
                decimalFormat = addEditProductState.getCurrentUserPreferences().moneyDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onPriceValueChanged(fieldValue)
    }

    private fun calculateTotal() {
        val quantity = addEditProductState.quantityValue.toFloatOrZero()
        val price = addEditProductState.priceValue.toFloatOrZero()
        val fieldValue = if (quantity <= 0f || price <= 0f) {
            "".toTextFieldValue()
        } else {
            val totalValue = quantity * price
            val moneyDiscount = Money(
                value = addEditProductState.discountValue.toFloatOrZero(),
                asPercent = addEditProductState.discountAsPercentValue.selected
            )
            val taxRate = addEditProductState.getCurrentUserPreferences().taxRate
            val totalWithDiscountAndTaxRate = totalValue - moneyDiscount.calculateValueFromPercent(totalValue) +
                    taxRate.calculateValueFromPercent(totalValue)
            Money(
                value = totalWithDiscountAndTaxRate,
                currency = addEditProductState.getCurrentUserPreferences().currency,
                asPercent = false,
                decimalFormat = addEditProductState.getCurrentUserPreferences().moneyDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onTotalValueChanged(fieldValue)
    }
}