package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data39.suggestions.AddSuggestionWithDetails
import ru.sokolovromann.myshopping.data39.suggestions.Suggestion
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetail
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailValue
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDirectory
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionWithDetails
import ru.sokolovromann.myshopping.manager.Api15Manager
import ru.sokolovromann.myshopping.manager.SuggestionsManager
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.AddEditProductState
import ru.sokolovromann.myshopping.ui.model.SuggestionsSelectedValue
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toBigDecimalOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import ru.sokolovromann.myshopping.utils.math.DecimalExtensions.toDecimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams
import ru.sokolovromann.myshopping.utils.math.DiscountType
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val suggestionsManager: SuggestionsManager,
    private val shoppingListsRepository: ShoppingListsRepository,
    private val appConfigRepository: AppConfigRepository,
    private val api15Manager: Api15Manager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditProductEvent> {

    val addEditProductState: AddEditProductState = AddEditProductState()

    private val _screenEventFlow: MutableSharedFlow<AddEditProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditProductScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    private var tempNewSuggestion: Suggestion? = null

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

    private fun onInit() = viewModelScope.launch(dispatcher) {
        tempNewSuggestion = null

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
                delay(50L)
                _screenEventFlow.emit(AddEditProductScreenEvent.OnShowKeyboard)
            } else {
                getAutocompletes(productWithConfig.product.name)
            }
        }
    }

    private fun onClickSave() = viewModelScope.launch(dispatcher) {
        addEditProductState.onWaiting()

        val product = addEditProductState.getCurrentProduct()
        shoppingListsRepository.saveProduct(product)
            .onSuccess {
                when (suggestionsManager.getConfig().add) {
                    AddSuggestionWithDetails.Suggestion -> {
                        saveSuggestion()
                    }
                    AddSuggestionWithDetails.SuggestionAndDetails -> {
                        saveSuggestion()
                        saveSuggestionDetails()
                    }
                    AddSuggestionWithDetails.DoNotAdd -> {}
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

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
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
        addEditProductState.onNameSelected(event.name)
        getAutocompletes(event.name)
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
        addEditProductState.onQuantitySelected(event.quantity, event.symbol)
        calculatePriceAndTotal()
    }

    private fun onClickMinusOneQuantity() {
        val value = addEditProductState.quantityValue.toBigDecimalOrZero().minus(BigDecimal.ONE)
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
        val value = addEditProductState.quantityValue.toBigDecimalOrZero().plus(BigDecimal.ONE)
        val quantityValue = Quantity(value = value).toTextFieldValue()
        addEditProductState.onQuantityValueChanged(quantityValue)
        calculatePriceAndTotal()
    }

    private fun onQuantitySymbolValueChanged(event: AddEditProductEvent.OnQuantitySymbolValueChanged) {
        addEditProductState.onQuantitySymbolValueChanged(event.value)
    }

    private fun onQuantitySymbolSelected(event: AddEditProductEvent.OnQuantitySymbolSelected) {
        addEditProductState.onQuantitySymbolSelected(event.symbol)
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
        addEditProductState.onDiscountSelected(event.discount, event.type)
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
        val price = addEditProductState.priceValue.toBigDecimalOrZero()
        val total = addEditProductState.totalValue.toBigDecimalOrZero()
        val fieldValue = if (price.toFloat() <= 0f || total.toFloat() <= 0f) {
            "".toTextFieldValue()
        } else {
            Quantity(
                value = total.divide(price, 3, RoundingMode.HALF_UP),
                decimalFormat = addEditProductState.getCurrentUserPreferences().quantityDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onQuantityValueChanged(fieldValue)
    }

    private fun onLockPrice() {
        val quantity = addEditProductState.quantityValue.toBigDecimalOrZero()
        val total = addEditProductState.totalValue.toBigDecimalOrZero()
        val fieldValue = if (quantity.toFloat() <= 0f || total.toFloat() < 0f) {
            "".toTextFieldValue()
        } else {
            Money(
                value = total.divide(quantity, 2, RoundingMode.HALF_UP),
                currency = addEditProductState.getCurrentUserPreferences().currency,
                asPercent = false,
                decimalFormat = addEditProductState.getCurrentUserPreferences().moneyDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onPriceValueChanged(fieldValue)
    }

    private fun onLockTotal() {
        val quantity = addEditProductState.quantityValue.toBigDecimalOrZero()
        val price = addEditProductState.priceValue.toBigDecimalOrZero()
        val fieldValue = if (quantity.toFloat() <= 0f || price.toFloat() <= 0f) {
            "".toTextFieldValue()
        } else {
            val totalValue = quantity.multiply(price)
            val moneyDiscount = Money(
                value = addEditProductState.discountValue.toBigDecimalOrZero(),
                asPercent = addEditProductState.discountAsPercentValue.selected
            )
            val taxRate = addEditProductState.getCurrentUserPreferences().taxRate
            val totalWithDiscount = totalValue.minus(moneyDiscount.calculateValueFromPercent(totalValue))
            val totalWithTaxRate = totalWithDiscount.plus(taxRate.calculateValueFromPercent(totalWithDiscount))
            Money(
                value = totalWithTaxRate,
                currency = addEditProductState.getCurrentUserPreferences().currency,
                asPercent = false,
                decimalFormat = addEditProductState.getCurrentUserPreferences().moneyDecimalFormat
            ).toTextFieldValue()
        }

        addEditProductState.onTotalValueChanged(fieldValue)
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch(dispatcher) {
        val suggestions = suggestionsManager.findSuggestionsWithDetails(name)
        if (suggestions.isEmpty()) {
            addEditProductState.onHideAutocompletes()
        } else {
            val suggestionsSelected = createSuggestionsSelectedValueAndUids(name, suggestions)
            addEditProductState.onShowAutocomplete(suggestionsSelected)
        }
    }

    private fun createSuggestionsSelectedValueAndUids(
        name: String,
        suggestions: Collection<SuggestionWithDetails>
    ): SuggestionsSelectedValue {
        val selectedSuggestion = suggestions.find { it.suggestion.name.equals(name, true) }
        val mappedSuggestions = if (selectedSuggestion == null) {
            suggestions.map { it.suggestion }
        } else emptyList()

        val mappedBrands = if (addEditProductState.brandValue.isEmpty()) {
            suggestions.flatMap { it.details.brands }
        } else emptyList()

        val mappedSizes = if (addEditProductState.sizeValue.isEmpty()) {
            suggestions.flatMap { it.details.sizes }
        } else emptyList()

        val mappedColors = if (addEditProductState.colorValue.isEmpty()) {
            suggestions.flatMap { it.details.colors }
        } else emptyList()

        val mappedManufacturers = if (addEditProductState.manufacturerValue.isEmpty()) {
            suggestions.flatMap { it.details.manufacturers }
        } else emptyList()

        val mappedQuantities = if (addEditProductState.quantityValue.isEmpty()) {
            suggestions.flatMap { it.details.quantities }
        } else emptyList()

        val displayDefaultQuantitySymbols = mappedQuantities.isEmpty() &&
                addEditProductState.quantitySymbolValue.isEmpty()

        val mappedPrices = if (addEditProductState.priceValue.isEmpty()) {
            suggestions.flatMap { it.details.unitPrices }
        } else emptyList()

        val mappedDiscounts = if (addEditProductState.discountValue.isEmpty()) {
            suggestions.flatMap { it.details.discounts }
        } else emptyList()

        val mappedTotals = if (addEditProductState.totalValue.isEmpty()) {
            suggestions.flatMap { it.details.costs }
        } else emptyList()

        val currency = addEditProductState.getCurrentUserPreferences().currency
        val quantityDecimalFormat = addEditProductState.quantityDecimalFormat
        val moneyDecimalFormat = addEditProductState.moneyDecimalFormat

        return SuggestionsSelectedValue(
            suggestionUid = selectedSuggestion?.suggestion?.uid,
            names = UiAutocompletesMapper.toUiNames(mappedSuggestions),
            brands = UiAutocompletesMapper.toUiBrands(mappedBrands),
            sizes = UiAutocompletesMapper.toUiSizes(mappedSizes),
            colors = UiAutocompletesMapper.toUiColors(mappedColors),
            manufacturers = UiAutocompletesMapper.toUiManufacturers(mappedManufacturers),
            quantities = UiAutocompletesMapper.toUiQuantities(mappedQuantities, quantityDecimalFormat),
            quantitySymbols = UiAutocompletesMapper.toUiQuantitiesSymbols(mappedQuantities),
            displayDefaultQuantitySymbols = displayDefaultQuantitySymbols,
            prices = UiAutocompletesMapper.toUiPrices(mappedPrices, currency, moneyDecimalFormat),
            discounts = UiAutocompletesMapper.toUiDiscounts(mappedDiscounts, currency, moneyDecimalFormat),
            totals = UiAutocompletesMapper.toUiTotals(mappedTotals, currency, moneyDecimalFormat)
        )
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

    private fun getSelectedSuggestionUid(): UID {
        return addEditProductState.suggestionsValue.suggestionUid ?: UID.createFromString("")
    }

    private suspend fun saveSuggestion(): Unit = withIoContext {
        val oldSuggestion = suggestionsManager.getSuggestionWithDetails(getSelectedSuggestionUid())?.suggestion
        val currentDateTime = DateTime.getCurrent()
        val newSuggestion = oldSuggestion?.copy(
            lastModified = currentDateTime,
            used = oldSuggestion.used.plus(1)
        ) ?: Suggestion(
            uid = UID.createRandom(),
            directory = SuggestionDirectory.NoDirectory,
            created = currentDateTime,
            lastModified = currentDateTime,
            name = addEditProductState.nameValue.text,
            used = 1
        )
        api15Manager.addAutocomplete(newSuggestion)
        suggestionsManager.addSuggestion(newSuggestion)

        tempNewSuggestion = newSuggestion
    }

    private suspend fun saveSuggestionDetails(): Unit = withIoContext {
        val details = mutableListOf<SuggestionDetail>().apply {
            suggestionsManager.getSuggestionWithDetails(getSelectedSuggestionUid())?.let { data ->
                createBrand(data)?.let { add(it) }
                createSize(data)?.let { add(it) }
                createColor(data)?.let { add(it) }
                createManufacturer(data)?.let { add(it) }
                createQuantity(data)?.let { add(it) }
                createPrice(data)?.let { add(it) }
                createDiscount(data)?.let { add(it) }
                createTotal(data)?.let { add(it) }
            }
        }
        suggestionsManager.addDetails(details)
        tempNewSuggestion?.let {
            api15Manager.addAutocompletes(it, details)
        }
    }

    private fun createBrand(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Brand? {
        val currentBrand = addEditProductState.brandValue.text.trim()
        if (currentBrand.isEmpty()) return null

        val foundBrand = suggestionWithDetails.details.brands.find {
            it.value.data.equals(currentBrand, true)
        }
        val newValue = foundBrand?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundBrand.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentBrand)
        return SuggestionDetail.Brand(newValue)
    }

    private fun createSize(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Size? {
        val currentSize = addEditProductState.sizeValue.text.trim()
        if (currentSize.isEmpty()) return null

        val foundSize = suggestionWithDetails.details.sizes.find {
            it.value.data.equals(currentSize, true)
        }
        val newValue = foundSize?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundSize.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentSize)
        return SuggestionDetail.Size(newValue)
    }

    private fun createColor(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Color? {
        val currentColor = addEditProductState.colorValue.text.trim()
        if (currentColor.isEmpty()) return null

        val foundColor = suggestionWithDetails.details.colors.find {
            it.value.data.equals(currentColor, true)
        }
        val newValue = foundColor?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundColor.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentColor)
        return SuggestionDetail.Color(newValue)
    }

    private fun createManufacturer(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Manufacturer? {
        val currentManufacturer = addEditProductState.manufacturerValue.text.trim()
        if (currentManufacturer.isEmpty()) return null

        val foundManufacturer = suggestionWithDetails.details.manufacturers.find {
            it.value.data.equals(currentManufacturer, true)
        }
        val newValue = foundManufacturer?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundManufacturer.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentManufacturer)
        return SuggestionDetail.Manufacturer(newValue)
    }

    private fun createQuantity(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Quantity? {
        val currentQuantity = addEditProductState.quantityValue.text.trim()
        val currentQuantitySymbol = addEditProductState.quantitySymbolValue.text.trim()
        if (currentQuantity.isEmpty() && currentQuantitySymbol.isEmpty()) return null

        val foundQuantity = suggestionWithDetails.details.quantities.find {
            val data = it.value.data
            data.decimal.toFloatOrZero() == currentQuantity.toFloatOrNull() &&
                    data.params.equals(currentQuantitySymbol, true)
        }
        val newValue = foundQuantity?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundQuantity.value.used.plus(1)
        ) ?: createDetailValue(
            suggestionWithDetails.suggestion.uid,
            DecimalWithParams(currentQuantity.toDecimal(), currentQuantitySymbol)
        )
        return SuggestionDetail.Quantity(newValue)
    }

    private fun createPrice(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.UnitPrice? {
        val currentPrice = addEditProductState.priceValue.text.trim()
        if (currentPrice.isEmpty()) return null

        val foundPrice = suggestionWithDetails.details.unitPrices.find {
            it.value.data.toFloatOrZero() == currentPrice.toFloatOrNull()
        }
        val newValue = foundPrice?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundPrice.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentPrice.toDecimal())
        return SuggestionDetail.UnitPrice(newValue)
    }

    private fun createDiscount(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Discount? {
        val currentDiscount = addEditProductState.discountValue.text.trim()
        if (currentDiscount.isEmpty()) return null

        val currentDiscountAsPercent = if (addEditProductState.discountAsPercentValue.selected) {
            DiscountType.Percent
        } else {
            DiscountType.Money
        }
        val foundDiscount = suggestionWithDetails.details.discounts.find {
            val data = it.value.data
            data.decimal.toFloatOrZero() == currentDiscount.toFloatOrNull() &&
                    data.params == currentDiscountAsPercent
        }
        val newValue = foundDiscount?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundDiscount.value.used.plus(1)
        ) ?: createDetailValue(
            suggestionWithDetails.suggestion.uid,
            DecimalWithParams(currentDiscount.toDecimal(), currentDiscountAsPercent)
        )
        return SuggestionDetail.Discount(newValue)
    }

    private fun createTotal(suggestionWithDetails: SuggestionWithDetails): SuggestionDetail.Cost? {
        val currentTotal = addEditProductState.totalValue.text.trim()
        if (currentTotal.isEmpty()) return null

        val foundTotal = suggestionWithDetails.details.costs.find {
            it.value.data.toFloatOrZero() == currentTotal.toFloatOrNull()
        }
        val newValue = foundTotal?.value?.copy(
            lastModified = DateTime.getCurrent(),
            used = foundTotal.value.used.plus(1)
        ) ?: createDetailValue(suggestionWithDetails.suggestion.uid, currentTotal.toDecimal())
        return SuggestionDetail.Cost(newValue)
    }

    private fun<T> createDetailValue(directory: UID, data: T): SuggestionDetailValue<T> {
        val currentDateTime = DateTime.getCurrent()
        return SuggestionDetailValue(
            uid = UID.createRandom(),
            directory = directory,
            created = currentDateTime,
            lastModified = currentDateTime,
            data = data,
            used = 1
        )
    }
}