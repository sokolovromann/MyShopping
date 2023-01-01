package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    val addEditProductState: AddEditProductState = AddEditProductState()

    private val _quantityMinusOneState: MutableState<TextData> = mutableStateOf(TextData())
    val quantityMinusOneState: State<TextData> = _quantityMinusOneState

    private val _quantityPlusOneState: MutableState<TextData> = mutableStateOf(TextData())
    val quantityPlusOneState: State<TextData> = _quantityPlusOneState

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
            val currentName = addEditProductState.screenData.nameValue.text
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
        val product = addEditProductState.getProductResult()
            .getOrElse { return@launch }

        if (productUid == null) {
            repository.addProduct(product)
        } else {
            repository.editProduct(product)
        }

        addEditProductState.getAutocompleteResult()
            .onSuccess { repository.addAutocomplete(it) }

        hideKeyboard()
        showBackScreen()
    }

    private fun cancelSavingProduct() = viewModelScope.launch(dispatchers.main) {
        showBackScreen()
    }

    private fun productNameChanged(event: AddEditProductEvent.ProductNameChanged) {
        addEditProductState.changeNameValue(event.value)

        val minLength = 2
        val displayAutocomplete = addEditProductState.getDisplayAutocomplete()
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

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(AddEditProductScreenEvent.ShowBackScreen)
    }

    private suspend fun showAddEditProduct(
        addEditProduct: AddEditProduct
    ) = withContext(dispatchers.main) {
        if (productUid == null) {
            val product = Product(shoppingUid = shoppingUid ?: "")
            addEditProductState.populate(addEditProduct.copy(product = product))
        } else {
            addEditProductState.populate(addEditProduct)
        }

        val product = addEditProduct.product ?: Product()
        val preferences = addEditProduct.preferences

        val name = product.name.formatFirst(preferences.firstLetterUppercase)

        if (productUid == null) {
            showKeyboard()
        } else {
            hideKeyboard()
            getAutocompletes(name)
        }
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

    private fun showProductDiscountAsPercentMenu() {
        addEditProductState.showDiscountAsPercent()
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
            text = mapping.toResourcesUiText(R.string.addEditProduct_action_saveProduct),
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
        addEditProductState.showAutocompleteNames(addEditProductAutocomplete.names())
    }

    private suspend fun showAutocompleteQuantities(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        addEditProductState.showAutocompleteQuantities(
            autocompleteQuantities = addEditProductAutocomplete.quantities(currentName),
            autocompleteQuantitySymbols = addEditProductAutocomplete.quantitySymbols(currentName)
        )
    }

    private suspend fun showAutocompletePrices(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        addEditProductState.showAutocompletePrices(addEditProductAutocomplete.prices(currentName))
    }

    private suspend fun showAutocompleteDiscounts(
        addEditProductAutocomplete: AddEditProductAutocomplete
    ) = withContext(dispatchers.main) {
        val currentName = addEditProductState.screenData.nameValue.text
        addEditProductState.showAutocompleteDiscounts(addEditProductAutocomplete.discounts(currentName))
    }

    private fun hideProductDiscountAsPercentMenu() {
        addEditProductState.hideDiscountAsPercent()
    }

    private fun hideKeyboard() = viewModelScope.launch(dispatchers.main) {
        _keyboardFlow.emit(false)
    }

    private fun hideAutocompleteNames() {
        addEditProductState.hideAutocompleteNames()
    }

    private fun hideAutocompleteQuantities() {
        addEditProductState.hideAutocompleteQuantities()
    }

    private fun hideAutocompletePrices() {
        addEditProductState.hideAutocompletePrices()
    }

    private fun hideAutocompleteDiscounts() {
        addEditProductState.hideAutocompleteDiscounts()
    }
}