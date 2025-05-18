package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
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
import ru.sokolovromann.myshopping.ui.model.AutocompletesSelectedValue
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.isNotEmpty
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

    init { onInit() }

    override fun onEvent(event: AddEditProductEvent) {
        when (event) {
            AddEditProductEvent.OnClickSave -> onClickSave()

            AddEditProductEvent.OnClickCancel -> onClickCancel()

            is AddEditProductEvent.OnNameValueChanged -> onNameValueChanged(event)

            is AddEditProductEvent.OnNameSelected -> onNameSelected(event)

            AddEditProductEvent.OnInvertNameOtherFields -> onInvertNameOtherFields()

            is AddEditProductEvent.OnUidValueChanged -> onUidValueChanged(event)

            is AddEditProductEvent.OnBrandValueChanged -> onBrandValueChanged(event)

            is AddEditProductEvent.OnBrandSelected -> onBrandSelected(event)

            is AddEditProductEvent.OnSizeValueChanged -> onSizeValueChanged(event)

            is AddEditProductEvent.OnSizeSelected -> onSizeSelected(event)

            is AddEditProductEvent.OnColorValueChanged -> onColorValueChanged(event)

            is AddEditProductEvent.OnColorSelected -> onColorSelected(event)

            is AddEditProductEvent.OnManufacturerValueChanged -> onManufacturerValueChanged(event)

            is AddEditProductEvent.OnManufacturerSelected -> onManufacturerSelected(event)

            is AddEditProductEvent.OnQuantityValueChanged -> onQuantityValueChanged(event)

            is AddEditProductEvent.OnQuantitySelected -> onQuantitySelected(event)

            AddEditProductEvent.OnClickMinusOneQuantity -> onClickMinusOneQuantity()

            AddEditProductEvent.OnClickPlusOneQuantity -> onClickPlusOneQuantity()

            is AddEditProductEvent.OnQuantitySymbolValueChanged -> onQuantitySymbolValueChanged(event)

            is AddEditProductEvent.OnQuantitySymbolSelected -> onQuantitySymbolSelected(event)

            is AddEditProductEvent.OnPriceValueChanged -> onPriceValueChanged(event)

            is AddEditProductEvent.OnPriceSelected -> onPriceSelected(event)

            AddEditProductEvent.OnInvertPriceOtherFields -> onInvertPriceOtherFields()

            is AddEditProductEvent.OnDiscountValueChanged -> onDiscountValueChanged(event)

            is AddEditProductEvent.OnDiscountSelected -> onDiscountSelected(event)

            is AddEditProductEvent.OnDiscountAsPercentSelected -> onDiscountAsPercentSelected(event)

            is AddEditProductEvent.OnSelectDiscountAsPercent -> onSelectDiscountAsPercent(event)

            is AddEditProductEvent.OnTotalValueChanged -> onTotalValueChanged(event)

            is AddEditProductEvent.OnTotalSelected -> onTotalSelected(event)

            is AddEditProductEvent.OnNoteValueChanged -> onNoteValueChanged(event)

            is AddEditProductEvent.OnLockProductElementSelected -> onLockProductElementSelected(event)

            is AddEditProductEvent.OnSelectLockProductElement -> onSelectLockProductElement(event)
        }
    }

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        val productUid = savedStateHandle.get<String>(UiRouteKey.ProductUid.key)
        val shoppingUid = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key) ?: ""
        val isFromPurchases = savedStateHandle.get<String>(UiRouteKey.IsFromPurchases.key) ?: "false"

        shoppingListsRepository.getProductWithConfig(productUid).firstOrNull()?.let {
            val productWithConfig = if (productUid != null) it else {
                val product = Product(shoppingUid = shoppingUid)
                it.copy(product = product)
            }
            addEditProductState.populate(productWithConfig, isFromPurchases.toBoolean())

            if (productUid == null) {
                _screenEventFlow.emit(AddEditProductScreenEvent.OnShowKeyboard)
            } else {
                getAutocompletes(productWithConfig.product.name)
            }
        }
    }

    private fun onClickSave() = viewModelScope.launch(AppDispatchers.Main) {
        addEditProductState.onWaiting()

        val product = addEditProductState.getCurrentProduct()
        shoppingListsRepository.saveProduct(product)
            .onSuccess {
                if (addEditProductState.getCurrentUserPreferences().saveProductToAutocompletes) {
                    autocompletesRepository.saveAutocomplete(addEditProductState.getCurrentAutocomplete())
                }
                appConfigRepository.lockProductElement(addEditProductState.lockProductElementValue.selected)

                val isNewProduct = savedStateHandle.get<String>(UiRouteKey.ProductUid.key) == null
                val event = if (isNewProduct) {
                    when (addEditProductState.afterSaveProduct) {
                        AfterSaveProduct.CLOSE_SCREEN -> {
                            if (addEditProductState.isFromPurchases) {
                                AddEditProductScreenEvent.OnShowProductsScreen(product.shoppingUid)
                            } else {
                                AddEditProductScreenEvent.OnShowBackScreen(product.shoppingUid)
                            }
                        }
                        AfterSaveProduct.OPEN_NEW_SCREEN -> {
                            AddEditProductScreenEvent.OnShowNewScreen(
                                shoppingUid = product.shoppingUid,
                                isFromPurchases = addEditProductState.isFromPurchases
                            )
                        }
                        AfterSaveProduct.NOTHING -> {
                            AddEditProductScreenEvent.OnUpdateProductsWidget(product.shoppingUid)
                        }
                    }
                } else {
                    AddEditProductScreenEvent.OnShowBackScreen(product.shoppingUid)
                }
                _screenEventFlow.emit(event)
            }
            .onFailure {
                when (it) {
                    is InvalidNameException -> { addEditProductState.onInvalidNameValue() }
                    is InvalidUidException -> { addEditProductState.onInvalidUidValue() }
                    else -> {}
                }
            }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        val shoppingUid = addEditProductState.getCurrentProduct().shoppingUid
        val event = if (addEditProductState.isFromPurchases) {
            AddEditProductScreenEvent.OnShowProductsScreen(shoppingUid)
        } else {
            AddEditProductScreenEvent.OnShowBackScreen(shoppingUid)
        }
        _screenEventFlow.emit(event)
    }

    private fun onNameValueChanged(event: AddEditProductEvent.OnNameValueChanged) {
        addEditProductState.onNameValueChanged(event.value)

        val minLength = 2
        if (event.value.text.length >= minLength) {
            val search = event.value.text
            getAutocompletes(search)
        } else {
            addEditProductState.onHideAutocompletes()
        }
    }

    private fun onNameSelected(event: AddEditProductEvent.OnNameSelected) {
        addEditProductState.onNameSelected(event.autocomplete)
        getAutocompletes(event.autocomplete.name)
    }

    private fun onInvertNameOtherFields() {
        addEditProductState.onInvertNameOtherFields()
    }

    private fun onUidValueChanged(event: AddEditProductEvent.OnUidValueChanged) {
        addEditProductState.onUidValueChanged(event.value)
    }

    private fun onBrandValueChanged(event: AddEditProductEvent.OnBrandValueChanged) {
        addEditProductState.onBrandValueChanged(event.value)
    }

    private fun onBrandSelected(event: AddEditProductEvent.OnBrandSelected) {
        addEditProductState.onBrandSelected(event.brand)
    }

    private fun onSizeValueChanged(event: AddEditProductEvent.OnSizeValueChanged) {
        addEditProductState.onSizeValueChanged(event.value)
    }

    private fun onSizeSelected(event: AddEditProductEvent.OnSizeSelected) {
        addEditProductState.onSizeSelected(event.size)
    }

    private fun onColorValueChanged(event: AddEditProductEvent.OnColorValueChanged) {
        addEditProductState.onColorValueChanged(event.value)
    }

    private fun onColorSelected(event: AddEditProductEvent.OnColorSelected) {
        addEditProductState.onColorSelected(event.color)
    }

    private fun onManufacturerValueChanged(event: AddEditProductEvent.OnManufacturerValueChanged) {
        addEditProductState.onManufacturerValueChanged(event.value)
    }

    private fun onManufacturerSelected(event: AddEditProductEvent.OnManufacturerSelected) {
        addEditProductState.onManufacturerSelected(event.manufacturer)
    }

    private fun onQuantityValueChanged(event: AddEditProductEvent.OnQuantityValueChanged) {
        addEditProductState.onQuantityValueChanged(event.value)
        calculatePriceAndTotal()
    }

    private fun onQuantitySelected(event: AddEditProductEvent.OnQuantitySelected) {
        addEditProductState.onQuantitySelected(event.quantity)
        calculatePriceAndTotal()
    }

    private fun onClickMinusOneQuantity() {
        val value = addEditProductState.quantityValue.toFloatOrZero().minus(1)
        val quantity = Quantity(value = value)
        val quantityValue = if (quantity.isEmpty()) {
            "".toTextFieldValue()
        } else {
            quantity.toTextFieldValue()
        }
        addEditProductState.onQuantityValueChanged(quantityValue)
        calculatePriceAndTotal()
    }

    private fun onClickPlusOneQuantity() {
        val value = addEditProductState.quantityValue.toFloatOrZero().plus(1)
        val quantityValue = Quantity(value = value).toTextFieldValue()
        addEditProductState.onQuantityValueChanged(quantityValue)
        calculatePriceAndTotal()
    }

    private fun onQuantitySymbolValueChanged(event: AddEditProductEvent.OnQuantitySymbolValueChanged) {
        addEditProductState.onQuantitySymbolValueChanged(event.value)
    }

    private fun onQuantitySymbolSelected(event: AddEditProductEvent.OnQuantitySymbolSelected) {
        addEditProductState.onQuantitySymbolSelected(event.quantity)
    }

    private fun onPriceValueChanged(event: AddEditProductEvent.OnPriceValueChanged) {
        addEditProductState.onPriceValueChanged(event.value)
        calculateQuantityAndTotal()
    }

    private fun onPriceSelected(event: AddEditProductEvent.OnPriceSelected) {
        addEditProductState.onPriceSelected(event.price)
        calculateQuantityAndTotal()
    }

    private fun onInvertPriceOtherFields() {
        addEditProductState.onInvertPriceOtherFields()
    }

    private fun onDiscountValueChanged(event: AddEditProductEvent.OnDiscountValueChanged) {
        addEditProductState.onDiscountValueChanged(event.value)
        calculateTotal()
    }

    private fun onDiscountSelected(event: AddEditProductEvent.OnDiscountSelected) {
        addEditProductState.onDiscountSelected(event.discount)
        calculateTotal()
    }

    private fun onDiscountAsPercentSelected(event: AddEditProductEvent.OnDiscountAsPercentSelected) {
        addEditProductState.onDiscountAsPercentSelected(event.asPercent)
        calculateTotal()
    }

    private fun onSelectDiscountAsPercent(event: AddEditProductEvent.OnSelectDiscountAsPercent) {
        addEditProductState.onSelectDiscountAsPercent(event.expanded)
    }

    private fun onTotalValueChanged(event: AddEditProductEvent.OnTotalValueChanged) {
        addEditProductState.onTotalValueChanged(event.value)
        calculateQuantityAndPrice()
    }

    private fun onTotalSelected(event: AddEditProductEvent.OnTotalSelected) {
        addEditProductState.onTotalSelected(event.total)
        calculateQuantityAndPrice()
    }

    private fun onNoteValueChanged(event: AddEditProductEvent.OnNoteValueChanged) {
        addEditProductState.onNoteValueChanged(event.value)
    }

    private fun onLockProductElementSelected(event: AddEditProductEvent.OnLockProductElementSelected) {
        addEditProductState.onLockProductElementSelected(event.lockProductElement)
    }

    private fun onSelectLockProductElement(event: AddEditProductEvent.OnSelectLockProductElement) {
        addEditProductState.onSelectLockProductElement(event.expanded)
    }

    private fun onLockQuantity() {
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

    private fun onLockPrice() {
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

    private fun onLockTotal() {
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
            val totalWithDiscount = totalValue - moneyDiscount.calculateValueFromPercent(totalValue)
            val totalWithTaxRate = totalWithDiscount + taxRate.calculateValueFromPercent(totalWithDiscount)
            Money(
                value = totalWithTaxRate,
                currency = addEditProductState.getCurrentUserPreferences().currency,
                asPercent = false,
                decimalFormat = addEditProductState.getCurrentUserPreferences().moneyDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onTotalValueChanged(fieldValue)
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch(AppDispatchers.Main) {
        val search = name.trim()
        val autocompletes = autocompletesRepository.searchAutocompletesLikeName(
            search = search
        ).firstOrNull() ?: listOf()

        val autocompletesSelectedValue = createAutocompletesSelectedValue(autocompletes)
        if (autocompletesSelectedValue == null) {
            addEditProductState.onHideAutocompletes()
            return@launch
        } else {
            addEditProductState.onShowAutocomplete(autocompletesSelectedValue)
        }
    }

    private fun createAutocompletesSelectedValue(
        autocompletes: List<Autocomplete>
    ): AutocompletesSelectedValue? {
        val currentName = addEditProductState.nameValue.text
        val names = searchAutocompletesLikeName(autocompletes, currentName)
        if (names.isEmpty()) {
            return null
        }

        val containsAutocomplete = names.find { it.name.asSearchQuery() == currentName.asSearchQuery() }
        val filterByPersonal = filterAutocompletesByPersonal(autocompletes).sortedAutocompletes(
            Sort(SortBy.LAST_MODIFIED, false)
        )

        val filterNames = if (containsAutocomplete == null) names else listOf()
        val quantitySymbols = filterAutocompleteQuantitySymbols(filterByPersonal)
        val displayDefaultQuantitySymbols = quantitySymbols.isEmpty() &&
                addEditProductState.quantitySymbolValue.isEmpty()
        return addEditProductState.autocompletes.copy(
            names = filterNames,
            brands = filterAutocompleteBrands(filterByPersonal),
            sizes = filterAutocompleteSizes(filterByPersonal),
            colors = filterAutocompleteColors(filterByPersonal),
            manufacturers = filterAutocompleteManufacturers(filterByPersonal),
            quantities = filterAutocompleteQuantities(filterByPersonal),
            quantitySymbols = quantitySymbols,
            displayDefaultQuantitySymbols = displayDefaultQuantitySymbols,
            prices = filterAutocompletePrices(filterByPersonal),
            discounts = filterAutocompleteDiscounts(filterByPersonal),
            totals = filterAutocompleteTotals(filterByPersonal),
            selected = containsAutocomplete
        )
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
                autocomplete.name.isNotEmpty() && index < addEditProductState.getCurrentUserPreferences().maxAutocompletesNames
            }
    }

    private fun filterAutocompleteBrands(autocompletes: List<Autocomplete>): List<String> {
        return if (addEditProductState.brandValue.isNotEmpty()) listOf() else autocompletes
            .map { it.brand }
            .distinct()
            .filterIndexed { index, brand ->
                brand.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteSizes(autocompletes: List<Autocomplete>): List<String> {
        return if (addEditProductState.sizeValue.isNotEmpty()) listOf() else autocompletes
            .map { it.size }
            .distinct()
            .filterIndexed { index, size ->
                size.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteColors(autocompletes: List<Autocomplete>): List<String> {
        return if (addEditProductState.colorValue.isNotEmpty()) listOf() else autocompletes
            .map { it.color }
            .distinct()
            .filterIndexed { index, color ->
                color.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteManufacturers(autocompletes: List<Autocomplete>): List<String> {
        return if (addEditProductState.manufacturerValue.isNotEmpty()) listOf() else autocompletes
            .map { it.manufacturer }
            .distinct()
            .filterIndexed { index, manufacturer ->
                manufacturer.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesOthers
            }
    }

    private fun filterAutocompleteQuantities(autocompletes: List<Autocomplete>): List<Quantity> {
        return if (addEditProductState.quantityValue.isNotEmpty()) listOf() else autocompletes
            .map { it.quantity }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, quantity ->
                quantity.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompleteQuantitySymbols(autocompletes: List<Autocomplete>): List<Quantity> {
        return if (addEditProductState.quantitySymbolValue.isNotEmpty()) listOf() else autocompletes
            .map { it.quantity }
            .distinctBy { it.symbol }
            .filterIndexed { index, quantity ->
                quantity.value > 0 && quantity.symbol.isNotEmpty() &&
                        index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesQuantities
            }
    }

    private fun filterAutocompletePrices(autocompletes: List<Autocomplete>): List<Money> {
        return if (addEditProductState.priceValue.isNotEmpty()) listOf() else autocompletes
            .map { it.price }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, price ->
                price.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompleteDiscounts(autocompletes: List<Autocomplete>): List<Money> {
        return if (addEditProductState.discountValue.isNotEmpty()) listOf() else autocompletes
            .map { it.discount }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, discount ->
                discount.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun filterAutocompleteTotals(autocompletes: List<Autocomplete>): List<Money> {
        return if (addEditProductState.totalValue.isNotEmpty()) listOf() else autocompletes
            .map { it.total }
            .distinctBy { it.getFormattedValue() }
            .filterIndexed { index, total ->
                total.isNotEmpty() && index <= addEditProductState.getCurrentUserPreferences().maxAutocompletesMoneys
            }
    }

    private fun calculatePriceAndTotal() {
        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.PRICE -> onLockPrice()
            LockProductElement.TOTAL -> onLockTotal()
            else -> {}
        }
    }

    private fun calculateQuantityAndTotal() {
        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> onLockQuantity()
            LockProductElement.TOTAL -> onLockTotal()
            else -> {}
        }
    }

    private fun calculateQuantityAndPrice() {
        when (addEditProductState.lockProductElementValue.selected) {
            LockProductElement.QUANTITY -> onLockQuantity()
            LockProductElement.PRICE -> onLockPrice()
            else -> {}
        }
    }

    private fun calculateTotal() {
        if (addEditProductState.lockProductElementValue.selected == LockProductElement.TOTAL) {
            onLockTotal()
        }
    }
}