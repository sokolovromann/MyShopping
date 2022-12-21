package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.AddEditProductRepository
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.compose.event.AddEditProductScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditProductEvent
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val repository: AddEditProductRepository,
    private val mapping: ViewModelMapping,
    private val dispatchers: AppDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ViewModelEvent<AddEditProductEvent> {

    private val addEditProductState: MutableState<AddEditProduct> = mutableStateOf(AddEditProduct())

    val nameState: TextFieldState = TextFieldState()

    val quantityState: TextFieldState = TextFieldState()

    val quantitySymbolState: TextFieldState = TextFieldState()

    private val _quantityMinusOneState: MutableState<TextData> = mutableStateOf(TextData())
    val quantityMinusOneState: State<TextData> = _quantityMinusOneState

    private val _quantityPlusOneState: MutableState<TextData> = mutableStateOf(TextData())
    val quantityPlusOneState: State<TextData> = _quantityPlusOneState

    val priceState: TextFieldState = TextFieldState()

    val discountState: TextFieldState = TextFieldState()

    val discountAsPercentState: MenuButtonState<DiscountAsPercentMenu> = MenuButtonState()

    val autocompleteNamesState: ListState<TextData> = ListState()

    val autocompleteQuantitiesState: ListState<QuantityItem> = ListState()

    val autocompleteQuantitySymbolsState: ListState<QuantityItem> = ListState()

    val autocompletePricesState: ListState<MoneyItem> = ListState()

    val autocompleteDiscountsState: ListState<DiscountItem> = ListState()

    private val _saveState: MutableState<TextData> = mutableStateOf(TextData())
    val saveState: State<TextData> = _saveState

    private val _topBarState: MutableState<TopBarData> = mutableStateOf(TopBarData())
    val topBarState: State<TopBarData> = _topBarState

    private val _keyboardFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val keyboardFlow: SharedFlow<Boolean> = _keyboardFlow

    private val _screenEventFlow: MutableSharedFlow<AddEditProductScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<AddEditProductScreenEvent> = _screenEventFlow

    private val shoppingUid: String? = savedStateHandle.get<String>(UiRouteKey.ShoppingUid.key)

    private val productUid: String? = savedStateHandle.get<String>(UiRouteKey.ProductUid.key)

    init {
        showTopBar()
        showSaveButton()
        showQuantityMinusOneButton()
        showQuantityPlusOneButton()
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

            is AddEditProductEvent.AutocompletePriceSelected -> autocompletePriceSelected(event)

            is AddEditProductEvent.AutocompleteDiscountSelected -> autocompleteDiscountSelected(event)

            AddEditProductEvent.AutocompleteMinusOneQuantitySelected -> autocompleteMinusOneQuantitySelected()

            AddEditProductEvent.AutocompletePlusOneQuantitySelected -> autocompletePlusOneQuantitySelected()

            AddEditProductEvent.ShowProductDiscountAsPercentMenu -> showProductDiscountAsPercentMenu()

            AddEditProductEvent.HideProductDiscountAsPercentMenu -> hideProductDiscountAsPercentMenu()

            else -> {}
        }
    }

    private fun getAddEditProduct() = viewModelScope.launch(dispatchers.io) {
        repository.getAddEditProduct(productUid).firstOrNull()?.let {
            showAddEditProduct(it)
        }
    }

    private fun getAutocompletes(name: String) = viewModelScope.launch(dispatchers.io) {
        val search = name.trim()
        val addEditProductAutocomplete = repository.getAutocompletes(search).firstOrNull()
            ?: AddEditProductAutocomplete()
        showAddEditProductAutocomplete(addEditProductAutocomplete)
    }

    private suspend fun showAddEditProductAutocomplete(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val autocompletes = addEditProductAutocomplete.names()

        if (autocompletes.isEmpty()) {
            hideAutocompleteNames()
            hideAutocompleteQuantities()
            hideAutocompletePrices()
            hideAutocompleteDiscounts()
        } else {
            val currentName = nameState.currentData.text.text
            val containsName = autocompletes.contains(currentName)

            when (addEditProductAutocomplete.preferences.displayAutocomplete) {
                DisplayAutocomplete.ALL -> {
                    if (containsName) {
                        hideAutocompleteNames()
                    } else {
                        showAutocompleteNames(addEditProductAutocomplete)
                    }

                    showAutocompleteQuantities(addEditProductAutocomplete)
                    showAutocompletePrices(addEditProductAutocomplete)
                    showAutocompleteDiscounts(addEditProductAutocomplete)
                }

                DisplayAutocomplete.NAME -> {
                    if (containsName) {
                        hideAutocompleteNames()
                    } else {
                        showAutocompleteNames(addEditProductAutocomplete)
                    }
                }

                DisplayAutocomplete.HIDE -> {
                    hideAutocompleteNames()
                    hideAutocompleteQuantities()
                    hideAutocompletePrices()
                    hideAutocompleteDiscounts()
                }
            }
        }
    }

    private fun saveProduct() = viewModelScope.launch(dispatchers.io) {
        if (nameState.isTextEmpty()) {
            nameState.showError(
                error = mapping.toBody(
                    text = mapping.toResourcesUiText(R.string.addEditProduct_message_nameError),
                    fontSize = FontSize.MEDIUM,
                    appColor = AppColor.Error
                )
            )
            return@launch
        }

        val name = nameState.currentData.text.text
        val quantity = Quantity(
            value = mapping.toFloat(quantityState.currentData.text) ?: 0f,
            symbol = mapping.toString(quantitySymbolState.currentData.text)
        )
        val price = Money(
            value = mapping.toFloat(priceState.currentData.text) ?: 0f,
            currency = addEditProductState.value.preferences.currency
        )
        val discount = Discount(
            value = mapping.toFloat(discountState.currentData.text) ?: 0f,
            asPercent = discountAsPercentState.currentData.menu?.asPercentSelected?.selected ?: true
        )
        val taxRate = addEditProductState.value.product?.taxRate ?: TaxRate()

        val product = addEditProductState.value.product?.copy(
            lastModified = System.currentTimeMillis(),
            name = name,
            quantity = quantity,
            price = price,
            discount = discount,
            taxRate = taxRate
        ) ?: Product(
            shoppingUid = shoppingUid ?: "",
            name = name,
            quantity = quantity,
            price = price,
            discount = discount,
            taxRate = taxRate
        )

        if (productUid == null) {
            repository.addProduct(product)
        } else {
            repository.editProduct(product)
        }

        if (addEditProductState.value.preferences.addLastProduct) {
            val autocomplete = Autocomplete(
                name = product.name,
                quantity = product.quantity,
                price = product.price,
                discount = product.discount,
                taxRate = product.taxRate
            )
            repository.addAutocomplete(autocomplete)
        }

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingProduct() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun productNameChanged(event: AddEditProductEvent.ProductNameChanged) {
        nameState.changeText(event.value)

        val minLength = 2
        val displayAutocomplete = addEditProductState.value.preferences.displayAutocomplete
        val hideAutocomplete = displayAutocomplete == DisplayAutocomplete.HIDE ||
                event.value.text.length < minLength

        if (hideAutocomplete) {
            hideAutocompleteNames()
            hideAutocompleteQuantities()
            hideAutocompletePrices()
            hideAutocompleteDiscounts()
        } else {
            getAutocompletes(event.value.text)
        }
    }

    private fun productQuantityChanged(event: AddEditProductEvent.ProductQuantityChanged) {
        quantityState.changeText(event.value)
    }

    private fun productQuantityChanged(change: Float, minus: Boolean) {
        val quantity = mapping.toFloat(quantityState.currentData.text) ?: 0f
        val newValue = if (minus) {
            if (quantity <= 1) 0f else quantity - change
        } else {
            quantity + change
        }

        val changeQuantity = Quantity(
            value = newValue,
            symbol = mapping.toString(quantitySymbolState.currentData.text)
        )

        val text: String = changeQuantity.valueToString()
        quantityState.changeText(mapping.toTextFieldValue(text))
    }

    private fun productQuantitySymbolChanged(event: AddEditProductEvent.ProductQuantitySymbolChanged) {
        quantitySymbolState.changeText(event.value)
    }

    private fun productPriceChanged(event: AddEditProductEvent.ProductPriceChanged) {
        priceState.changeText(event.value)
    }

    private fun productDiscountChanged(event: AddEditProductEvent.ProductDiscountChanged) {
        discountState.changeText(event.value)
    }

    private fun productDiscountAsPercentSelected() {
        val text = discountAsPercentState.currentData.text

        discountAsPercentState.showButton(
            text = text.copy(text = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)),
            menu = mapping.toDiscountAsPercentMenuMenu(
                asPercent = true,
                fontSize = addEditProductState.value.preferences.fontSize
            )
        )

        hideProductDiscountAsPercentMenu()
    }

    private fun productDiscountAsMoneySelected() {
        val text = discountAsPercentState.currentData.text

        discountAsPercentState.showButton(
            text = text.copy(text = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)),
            menu = mapping.toDiscountAsPercentMenuMenu(
                asPercent = false,
                fontSize = addEditProductState.value.preferences.fontSize
            )
        )

        hideProductDiscountAsPercentMenu()
    }

    private fun autocompleteNameSelected(event: AddEditProductEvent.AutocompleteNameSelected) {
        val text = mapping.toTextFieldValue(event.text)
        nameState.changeText(text)
        hideAutocompleteNames()
    }

    private fun autocompleteQuantitySelected(event: AddEditProductEvent.AutocompleteQuantitySelected) {
        if (event.quantity.isNotEmpty()) {
            val quantity = mapping.toTextFieldValue(event.quantity.valueToString())
            quantityState.changeText(quantity)
        }

        val quantitySymbol = mapping.toTextFieldValue(event.quantity.symbol)
        quantitySymbolState.changeText(quantitySymbol)

        hideAutocompleteQuantities()
    }

    private fun autocompletePriceSelected(event: AddEditProductEvent.AutocompletePriceSelected) {
        val text = mapping.toTextFieldValue(event.price.valueToString())
        priceState.changeText(text)
        hideAutocompletePrices()
    }

    private fun autocompleteDiscountSelected(event: AddEditProductEvent.AutocompleteDiscountSelected) {
        val text = mapping.toTextFieldValue(event.discount.valueToString())
        discountState.changeText(text)

        if (event.discount.asPercent) {
            productDiscountAsPercentSelected()
        } else {
            productDiscountAsMoneySelected()
        }

        hideAutocompleteDiscounts()
    }

    private fun autocompleteMinusOneQuantitySelected() {
        productQuantityChanged(change = 1f, minus = true)
    }

    private fun autocompletePlusOneQuantitySelected() {
        productQuantityChanged(change = 1f, minus = false)
    }

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
    }

    private suspend fun showAddEditProduct(
        addEditProduct: AddEditProduct
    ) = withContext(dispatchers.main) {
        addEditProductState.value = addEditProduct

        val product = addEditProduct.product ?: Product()
        val preferences = addEditProduct.preferences

        val name = product.name.formatFirst(preferences.firstLetterUppercase)
        showName(name, preferences)

        showQuantity(product.quantity, preferences)
        showQuantitySymbol(product.quantity, preferences)
        showPrice(product.price, preferences)
        showDiscount(product.discount, preferences)
        showProductDiscountAsPercentButton(product.discount, preferences)

        if (productUid == null) {
            showKeyboard()
        } else {
            hideKeyboard()
            getAutocompletes(name)
        }
    }

    private fun showName(name: String, preferences: ProductPreferences) {
        nameState.showTextField(
            text = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditProduct_label_name),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = if (preferences.firstLetterUppercase) {
                    KeyboardCapitalization.Sentences
                } else {
                    KeyboardCapitalization.None
                }
            )
        )
    }

    private fun showQuantity(quantity: Quantity, preferences: ProductPreferences) {
        val text: String = if (quantity.isEmpty()) "" else quantity.valueToString()

        quantityState.showTextField(
            text = mapping.toTextFieldValue(text),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditProduct_label_quantity),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None
            ),
            enabled = !preferences.lockQuantity
        )
    }

    private fun showQuantitySymbol(quantity: Quantity, preferences: ProductPreferences) {
        val text: String = if (quantity.isEmpty()) "" else quantity.symbol

        quantitySymbolState.showTextField(
            text = mapping.toTextFieldValue(text),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditProduct_label_quantitySymbol),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.None
            ),
            enabled = !preferences.lockQuantity
        )
    }

    private fun showQuantityMinusOneButton() {
        _quantityMinusOneState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.addEditProduct_quantityMinusOne),
            fontSize = FontSize.MEDIUM,
            appColor = AppColor.OnBackground
        )
    }

    private fun showQuantityPlusOneButton() {
        _quantityPlusOneState.value = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.addEditProduct_quantityPlusOne),
            fontSize = FontSize.MEDIUM,
            appColor = AppColor.OnBackground
        )
    }

    private fun showPrice(price: Money, preferences: ProductPreferences) {
        val text: String = if (price.isEmpty()) "" else price.valueToString()

        priceState.showTextField(
            text = mapping.toTextFieldValue(text),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditProduct_label_price),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None
            )
        )
    }

    private fun showDiscount(discount: Discount, preferences: ProductPreferences) {
        val text: String = if (discount.isEmpty()) "" else discount.valueToString()

        discountState.showTextField(
            text = mapping.toTextFieldValue(text),
            label = mapping.toBody(
                text = mapping.toResourcesUiText(R.string.addEditProduct_label_discount),
                fontSize = preferences.fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                capitalization = KeyboardCapitalization.None
            )
        )
    }

    private fun showProductDiscountAsPercentButton(
        discount: Discount,
        preferences: ProductPreferences
    ) {
        discountAsPercentState.showButton(
            text = mapping.toDiscountAsPercentBody(
                asPercent = discount.asPercent,
                fontSize = preferences.fontSize
            ),
            menu = mapping.toDiscountAsPercentMenuMenu(
                asPercent = discount.asPercent,
                fontSize = preferences.fontSize
            )
        )
    }

    private fun showProductDiscountAsPercentMenu() {
        discountAsPercentState.showMenu()
    }

    private fun showTopBar() {
        val data = TopBarData(
            navigationIcon = mapping.toOnTopAppBar(
                icon = UiIcon.FromVector(Icons.Default.ArrowBack)
            )
        )
        _topBarState.value = data
    }

    private fun showSaveButton() {
        val data = mapping.toBody(
            text = mapping.toResourcesUiText(R.string.addEditProduct_save),
            fontSize = FontSize.MEDIUM,
            appColor = AppColor.OnPrimary
        )
        _saveState.value = data
    }

    private fun showKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(true)
    }

    private suspend fun showAutocompleteNames(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val items = addEditProductAutocomplete.names().map {
            mapping.toAutocompleteItem(it, addEditProductAutocomplete.preferences)
        }

        autocompleteNamesState.showList(
            items = items,
            multiColumns = false
        )
    }

    private suspend fun showAutocompleteQuantities(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = nameState.currentData.text.text
        val quantityItems = addEditProductAutocomplete.quantities(currentName).map {
            mapping.toQuantityItem(it, addEditProductAutocomplete.preferences)
        }
        autocompleteQuantitiesState.showList(
            items = quantityItems,
            multiColumns = false
        )

        val symbolsItems = addEditProductAutocomplete.quantitySymbols(currentName).map {
            mapping.toQuantitySymbolItem(it, addEditProductAutocomplete.preferences)
        }
        autocompleteQuantitySymbolsState.showList(
            items = symbolsItems,
            multiColumns = false
        )
    }

    private suspend fun showAutocompletePrices(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = nameState.currentData.text.text
        val items = addEditProductAutocomplete.prices(currentName).map {
            mapping.toMoneyItem(it, addEditProductAutocomplete.preferences)
        }

        autocompletePricesState.showList(
            items = items,
            multiColumns = false
        )
    }

    private suspend fun showAutocompleteDiscounts(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = nameState.currentData.text.text
        val items = addEditProductAutocomplete.discounts(currentName).map {
            mapping.toDiscountItem(it, addEditProductAutocomplete.preferences)
        }

        autocompleteDiscountsState.showList(
            items = items,
            multiColumns = false
        )
    }

    private fun hideProductDiscountAsPercentMenu() {
        discountAsPercentState.hideMenu()
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }

    private fun hideAutocompleteNames() {
        autocompleteNamesState.hideAll()
    }

    private fun hideAutocompleteQuantities() {
        autocompleteQuantitiesState.hideAll()
        autocompleteQuantitySymbolsState.hideAll()
    }

    private fun hideAutocompletePrices() {
        autocompletePricesState.hideAll()
    }

    private fun hideAutocompleteDiscounts() {
        autocompleteDiscountsState.hideAll()
    }
}