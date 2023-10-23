package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrNull
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class AddEditProductState {

    private var selectedAutocomplete: Autocomplete? by mutableStateOf(null)

    var addEditProduct by mutableStateOf(AddEditProduct())

    var productNameFocus by mutableStateOf(false)
        private set

    var screenData by mutableStateOf(AddEditProductScreenData())
        private set

    fun populate(addEditProduct: AddEditProduct) {
        this.addEditProduct = addEditProduct

        val discountAsPercentText: UiText = if (addEditProduct.isDiscountAsPercent()) {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        }
        screenData = AddEditProductScreenData(
            screenState = ScreenState.Showing,
            nameValue = addEditProduct.getFieldName().toTextFieldValue(),
            showNameError = false,
            uidValue = addEditProduct.getFieldUid().toTextFieldValue(),
            showUidError = false,
            brandValue = addEditProduct.getFieldBrand().toTextFieldValue(),
            sizeValue = addEditProduct.getFieldSize().toTextFieldValue(),
            colorValue = addEditProduct.getFieldColor().toTextFieldValue(),
            manufacturerValue = addEditProduct.getFieldManufacturer().toTextFieldValue(),
            quantityValue = addEditProduct.getFieldQuantity().toTextFieldValue(),
            lockProductElement = addEditProduct.getLockProductElement(),
            quantitySymbolValue = addEditProduct.getFieldQuantitySymbol().toTextFieldValue(),
            priceValue = addEditProduct.getFieldPrice().toTextFieldValue(),
            discountValue = addEditProduct.getFieldDiscount().toTextFieldValue(),
            totalValue = addEditProduct.getFieldTotal().toTextFieldValue(),
            noteValue = addEditProduct.getFieldNote().toTextFieldValue(),
            discountAsPercent = addEditProduct.isDiscountAsPercent(),
            discountAsPercentText = discountAsPercentText,
            showDiscountAsPercent = false,
            fontSize = addEditProduct.getFontSize(),
            displayMoney = addEditProduct.isDisplayMoney(),
            enterToSaveProduct = addEditProduct.isEnterToSaveProduct(),
            displayOtherFields = addEditProduct.isDisplayOtherFields(),
            showNameOtherFields = addEditProduct.isDisplayNameOtherFields(),
            showPriceOtherFields = addEditProduct.isDisplayPriceOtherFields()
        )

        selectedAutocomplete = null
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun changeNameFocus(focused: Boolean) {
        productNameFocus = focused
    }

    fun changeUidValue(uidValue: TextFieldValue) {
        screenData = screenData.copy(
            uidValue = uidValue,
            showUidError = false
        )
    }

    fun changeBrandValue(brandValue: TextFieldValue) {
        screenData = screenData.copy(brandValue = brandValue)
    }

    fun changeSizeValue(sizeValue: TextFieldValue) {
        screenData = screenData.copy(sizeValue = sizeValue)
    }

    fun changeColorValue(colorValue: TextFieldValue) {
        screenData = screenData.copy(colorValue = colorValue)
    }

    fun changeManufacturerValue(manufacturerValue: TextFieldValue) {
        screenData = screenData.copy(manufacturerValue = manufacturerValue)
    }

    fun changeQuantityValue(quantityValue: TextFieldValue) {
        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun changeQuantitySymbolValue(quantitySymbolValue: TextFieldValue) {
        screenData = screenData.copy(quantitySymbolValue = quantitySymbolValue)
    }

    fun changePriceValue(priceValue: TextFieldValue) {
        screenData = screenData.copy(priceValue = priceValue)

        when (screenData.lockProductElement) {
            LockProductElement.QUANTITY -> setProductQuantityLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun changeDiscountValue(discountValue: TextFieldValue) {
        screenData = screenData.copy(discountValue = discountValue)

        if (screenData.lockProductElement == LockProductElement.TOTAL) {
            setProductTotalLock()
        }
    }

    fun changeProductTotalValue(totalValue: TextFieldValue) {
        screenData = screenData.copy(totalValue = totalValue)

        when (screenData.lockProductElement) {
            LockProductElement.QUANTITY -> setProductQuantityLock()
            LockProductElement.PRICE -> setProductPriceLock()
            else -> {}
        }
    }

    fun changeProductNoteValue(noteValue: TextFieldValue) {
        screenData = screenData.copy(noteValue = noteValue)
    }

    fun selectAutocompleteName(autocomplete: Autocomplete) {
        screenData = screenData.copy(
            nameValue = TextFieldValue(
                text = autocomplete.name,
                selection = TextRange(autocomplete.name.length),
                composition = TextRange(autocomplete.name.length)
            ),
            autocompleteNames = listOf()
        )

        selectedAutocomplete = autocomplete
    }

    fun selectAutocompleteBrand(brand: String) {
        screenData = screenData.copy(
            brandValue = TextFieldValue(
                text = brand,
                selection = TextRange(brand.length),
                composition = TextRange(brand.length)
            ),
            autocompleteBrands = listOf()
        )
    }

    fun selectAutocompleteSize(size: String) {
        screenData = screenData.copy(
            sizeValue = TextFieldValue(
                text = size,
                selection = TextRange(size.length),
                composition = TextRange(size.length)
            ),
            autocompleteSizes = listOf()
        )
    }

    fun selectAutocompleteColor(color: String) {
        screenData = screenData.copy(
            colorValue = TextFieldValue(
                text = color,
                selection = TextRange(color.length),
                composition = TextRange(color.length)
            ),
            autocompleteColors = listOf()
        )
    }

    fun selectAutocompleteManufacturer(manufacturer: String) {
        screenData = screenData.copy(
            manufacturerValue = TextFieldValue(
                text = manufacturer,
                selection = TextRange(manufacturer.length),
                composition = TextRange(manufacturer.length)
            ),
            autocompleteManufacturers = listOf()
        )
    }

    fun selectAutocompleteQuantity(quantity: Quantity) {
        val quantityText = quantity.getFormattedValueWithoutSeparators()
        val quantitySymbol = quantity.symbol

        screenData = screenData.copy(
            quantityValue = TextFieldValue(
                text = quantityText,
                selection = TextRange(quantityText.length),
                composition = TextRange(quantityText.length)
            ),
            quantitySymbolValue = TextFieldValue(
                text = quantitySymbol,
                selection = TextRange(quantitySymbol.length),
                composition = TextRange(quantitySymbol.length)
            ),
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf(),
        )

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun selectAutocompleteQuantitySymbol(quantity: Quantity) {
        val quantitySymbol = quantity.symbol
        screenData = screenData.copy(
            quantitySymbolValue = TextFieldValue(
                text = quantitySymbol,
                selection = TextRange(quantitySymbol.length),
                composition = TextRange(quantitySymbol.length)
            ),
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf()
        )
    }

    fun selectAutocompletePrice(price: Money) {
        val priceText = price.getFormattedValueWithoutSeparators()
        screenData = screenData.copy(
            priceValue = TextFieldValue(
                text = priceText,
                selection = TextRange(priceText.length),
                composition = TextRange(priceText.length)
            ),
            autocompletePrices = listOf()
        )

        when (screenData.lockProductElement) {
            LockProductElement.QUANTITY -> setProductQuantityLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun selectAutocompleteDiscount(discount: Money) {
        val discountText = discount.getFormattedValueWithoutSeparators()
        val discountAsPercentText: UiText = if (discount.asPercent) {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        }
        screenData = screenData.copy(
            discountValue = TextFieldValue(
                text = discountText,
                selection = TextRange(discountText.length),
                composition = TextRange(discountText.length)
            ),
            discountAsPercent = discount.asPercent,
            discountAsPercentText = discountAsPercentText,
            autocompleteDiscounts = listOf()
        )

        if (screenData.lockProductElement == LockProductElement.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectDiscountAsPercent() {
        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        screenData = screenData.copy(
            discountAsPercent = true,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )

        if (screenData.lockProductElement == LockProductElement.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectDiscountAsMoney() {
        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        screenData = screenData.copy(
            discountAsPercent = false,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )

        if (screenData.lockProductElement == LockProductElement.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectAutocompleteTotal(total: Money) {
        val totalText = total.getFormattedValueWithoutSeparators()
        screenData = screenData.copy(
            totalValue = TextFieldValue(
                text = totalText,
                selection = TextRange(totalText.length),
                composition = TextRange(totalText.length)
            ),
            autocompleteTotals = listOf()
        )

        when (screenData.lockProductElement) {
            LockProductElement.QUANTITY -> setProductQuantityLock()
            LockProductElement.PRICE -> setProductPriceLock()
            else -> {}
        }
    }

    fun lockProductElementSelected(lockProductElement: LockProductElement) {
        screenData = screenData.copy(
            lockProductElement = lockProductElement,
            showLockProductElement = false
        )
    }

    fun plusOneQuantity() {
        screenData = screenData.copy(
            quantityValue = addEditProduct.plusFieldQuantity(
                quantity = screenData.quantityValue.toFloatOrNull(),
                plus = 1
            ).toTextFieldValue()
        )

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun minusOneQuantity() {
        screenData = screenData.copy(
            quantityValue = addEditProduct.minusFieldQuantity(
                quantity = screenData.quantityValue.toFloatOrNull(),
                minus = 1
            ).toTextFieldValue()
        )

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun showAutocompleteNames(autocompleteNames: List<Autocomplete>) {
        screenData = screenData.copy(autocompleteNames = autocompleteNames)
    }

    fun showAutocompleteElements(
        brands: List<String>,
        sizes: List<String>,
        colors: List<String>,
        manufacturers: List<String>,
        quantities: List<Quantity>,
        quantitySymbols: List<Quantity>,
        prices: List<Money>,
        discounts: List<Money>,
        totals: List<Money>
    ) {
        val showDefaultQuantitySymbols = quantitySymbols.isEmpty() && screenData.quantitySymbolValue.isEmpty()
        screenData = screenData.copy(
            autocompleteBrands = brands,
            autocompleteSizes = sizes,
            autocompleteColors = colors,
            autocompleteManufacturers = manufacturers,
            autocompleteQuantities = quantities,
            autocompleteQuantitySymbols = quantitySymbols,
            showDefaultQuantitySymbols = showDefaultQuantitySymbols,
            autocompletePrices = prices,
            autocompleteDiscounts = discounts,
            autocompleteTotals = totals
        )
    }

    fun showAutocompleteElementsIf(
        brands: List<String>,
        sizes: List<String>,
        colors: List<String>,
        manufacturers: List<String>,
        quantities: List<Quantity>,
        quantitySymbols: List<Quantity>,
        prices: List<Money>,
        discounts: List<Money>,
        totals: List<Money>
    ) {
        val brandsIf = if (screenData.brandValue.isEmpty()) brands else listOf()
        val sizesIf = if (screenData.sizeValue.isEmpty()) sizes else listOf()
        val colorsIf = if (screenData.colorValue.isEmpty()) colors else listOf()
        val manufacturersIf = if (screenData.manufacturerValue.isEmpty()) manufacturers else listOf()
        val quantitiesIf = if (screenData.quantityValue.isEmpty()) quantities else listOf()
        val quantitySymbolsIf = if (screenData.quantitySymbolValue.isEmpty()) quantitySymbols else listOf()
        val pricesIf = if (screenData.priceValue.isEmpty()) prices else listOf()
        val discountsIf = if (screenData.discountValue.isEmpty()) discounts else listOf()
        val totalsIf = if (screenData.totalValue.isEmpty()) totals else listOf()

        showAutocompleteElements(
            brands = brandsIf,
            sizes = sizesIf,
            colors = colorsIf,
            manufacturers = manufacturersIf,
            quantities = quantitiesIf,
            quantitySymbols = quantitySymbolsIf,
            prices = pricesIf,
            discounts = discountsIf,
            totals = totalsIf
        )
    }

    fun showDiscountAsPercent() {
        screenData = screenData.copy(showDiscountAsPercent = true)
    }

    fun showNameError() {
        screenData = screenData.copy(showNameError = true)
    }

    fun showUidError() {
        screenData = screenData.copy(showUidError = true)
    }

    fun invertNameOtherFields() {
        val showFields = screenData.showNameOtherFields
        screenData = screenData.copy(showNameOtherFields = !showFields)
    }

    fun invertPriceOtherFields() {
        val showFields = screenData.showPriceOtherFields
        screenData = screenData.copy(showPriceOtherFields = !showFields)
    }

    fun selectLockProductElement() {
        screenData = screenData.copy(showLockProductElement = true)
    }

    fun hideAutocompleteNames(containsAutocomplete: Autocomplete) {
        screenData = screenData.copy(autocompleteNames = listOf())
        selectedAutocomplete = containsAutocomplete
    }

    fun hideAutocompletes() {
        screenData = screenData.copy(
            autocompleteNames = listOf(),
            autocompleteBrands = listOf(),
            autocompleteSizes = listOf(),
            autocompleteColors = listOf(),
            autocompleteManufacturers = listOf(),
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf(),
            showDefaultQuantitySymbols = screenData.quantitySymbolValue.isEmpty(),
            autocompletePrices = listOf(),
            autocompleteDiscounts = listOf(),
            autocompleteTotals = listOf()
        )
    }

    fun hideDiscountAsPercent() {
        screenData = screenData.copy(showDiscountAsPercent = false)
    }

    fun hideLockProductElement() {
        screenData = screenData.copy(showLockProductElement = false)
    }

    fun getProductResult(newProduct: Boolean): Result<Product> {
        return addEditProduct.createProduct(
            productUid = screenData.uidValue.text,
            name = screenData.nameValue.text,
            quantity = screenData.quantityValue.toFloatOrNull(),
            quantitySymbol = screenData.quantitySymbolValue.text,
            price = screenData.priceValue.toFloatOrNull(),
            discount = screenData.discountValue.toFloatOrNull(),
            discountAsPercent = screenData.discountAsPercent,
            total = screenData.totalValue.toFloatOrNull(),
            totalFormatted = screenData.lockProductElement != LockProductElement.TOTAL,
            note = screenData.noteValue.text,
            manufacturer = screenData.manufacturerValue.text,
            brand = screenData.brandValue.text,
            size = screenData.sizeValue.text,
            color = screenData.colorValue.text
        ).onFailure {
            when (it) {
                is InvalidNameException -> screenData = screenData.copy(showNameError = true)
                is InvalidUidException -> screenData = screenData.copy(showUidError = true)
                else -> {}
            }

            Result.failure<Product>(it)
        }.onSuccess {
            screenData = screenData.copy(screenState = ScreenState.Saving)
            Result.success(it)
        }
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        return addEditProduct.createAutocomplete(
            selectedName = selectedAutocomplete?.name,
            selectedPersonal = selectedAutocomplete?.personal,
            productName = screenData.nameValue.text,
            quantity = screenData.quantityValue.toFloatOrNull(),
            quantitySymbol = screenData.quantitySymbolValue.text,
            price = screenData.priceValue.toFloatOrNull(),
            discount = screenData.discountValue.toFloatOrNull(),
            discountAsPercent = screenData.discountAsPercent,
            total = screenData.totalValue.toFloatOrNull(),
            manufacturer = screenData.manufacturerValue.text,
            brand = screenData.brandValue.text,
            size = screenData.sizeValue.text,
            color = screenData.colorValue.text
        ).onFailure {
            Result.failure<Autocomplete>(it)
        }.onSuccess {
            Result.success(it)
        }
    }

    fun getProductLockResult(): Result<LockProductElement> {
        val success = screenData.lockProductElement
        return Result.success(success)
    }

    private fun setProductQuantityLock() {
        screenData = screenData.copy(
            quantityValue = addEditProduct.calculateFieldQuantity(
                price = screenData.priceValue.toFloatOrNull(),
                total = screenData.totalValue.toFloatOrNull()
            ).toTextFieldValue()
        )
    }

    private fun setProductPriceLock() {
        screenData = screenData.copy(
            priceValue = addEditProduct.calculateFieldPrice(
                quantity = screenData.quantityValue.toFloatOrNull(),
                total = screenData.totalValue.toFloatOrNull()
            ).toTextFieldValue()
        )
    }

    private fun setProductTotalLock() {
        screenData = screenData.copy(
            totalValue = addEditProduct.calculateFieldTotal(
                quantity = screenData.quantityValue.toFloatOrNull(),
                price = screenData.priceValue.toFloatOrNull(),
                discount = screenData.discountValue.toFloatOrNull(),
                discountAsPercent = screenData.discountAsPercent
            ).toTextFieldValue()
        )
    }
}

data class AddEditProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val showNameError: Boolean = false,
    val uidValue: TextFieldValue = TextFieldValue(),
    val showUidError: Boolean = false,
    val brandValue: TextFieldValue = TextFieldValue(),
    val sizeValue: TextFieldValue = TextFieldValue(),
    val colorValue: TextFieldValue = TextFieldValue(),
    val manufacturerValue: TextFieldValue = TextFieldValue(),
    val quantityValue: TextFieldValue = TextFieldValue(),
    val quantitySymbolValue: TextFieldValue = TextFieldValue(),
    val priceValue: TextFieldValue = TextFieldValue(),
    val discountValue: TextFieldValue = TextFieldValue(),
    val discountAsPercent: Boolean = true,
    val discountAsPercentText: UiText = UiText.Nothing,
    val showDiscountAsPercent: Boolean = false,
    val totalValue: TextFieldValue = TextFieldValue(),
    val lockProductElement: LockProductElement = LockProductElement.DefaultValue,
    val showLockProductElement: Boolean = false,
    val noteValue: TextFieldValue = TextFieldValue(),
    val autocompleteNames: List<Autocomplete> = listOf(),
    val autocompleteBrands: List<String> = listOf(),
    val autocompleteSizes: List<String> = listOf(),
    val autocompleteColors: List<String> = listOf(),
    val autocompleteManufacturers: List<String> = listOf(),
    val autocompleteQuantities: List<Quantity> = listOf(),
    val autocompleteQuantitySymbols: List<Quantity> = listOf(),
    val showDefaultQuantitySymbols: Boolean = true,
    val autocompletePrices: List<Money> = listOf(),
    val autocompleteDiscounts: List<Money> = listOf(),
    val autocompleteTotals: List<Money> = listOf(),
    val fontSize: FontSize = FontSize.MEDIUM,
    val displayMoney: Boolean = true,
    val enterToSaveProduct: Boolean = true,
    val displayOtherFields: Boolean = true,
    val showNameOtherFields: Boolean = false,
    val showPriceOtherFields: Boolean = false
)