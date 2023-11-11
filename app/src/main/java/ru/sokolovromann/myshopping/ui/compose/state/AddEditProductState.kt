package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.LockProductElement
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ProductWithConfig
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero
import ru.sokolovromann.myshopping.ui.utils.toTextFieldValue

class AddEditProductState {

    private var selectedAutocomplete: Autocomplete? by mutableStateOf(null)

    private var productWithConfig by mutableStateOf(ProductWithConfig())

    var productNameFocus by mutableStateOf(false)
        private set

    var screenData by mutableStateOf(AddEditProductScreenData())
        private set

    fun populate(productWithConfig: ProductWithConfig) {
        this.productWithConfig = productWithConfig

        val product =  productWithConfig.product
        val discount = product.discount
        val discountAsPercentText: UiText = if (discount.asPercent) {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        }
        val totalValue = if (product.quantity.isEmpty() && product.price.isNotEmpty()) {
            "".toTextFieldValue()
        } else {
            product.total.toTextFieldValue()
        }
        val userPreferences = productWithConfig.appConfig.userPreferences
        screenData = AddEditProductScreenData(
            screenState = ScreenState.Showing,
            nameValue = product.name.toTextFieldValue(),
            showNameError = false,
            uidValue = product.productUid.toTextFieldValue(),
            showUidError = false,
            brandValue = product.brand.toTextFieldValue(),
            sizeValue = product.size.toTextFieldValue(),
            colorValue = product.color.toTextFieldValue(),
            manufacturerValue = product.manufacturer.toTextFieldValue(),
            quantityValue = product.quantity.toTextFieldValue(),
            lockProductElement = userPreferences.lockProductElement,
            quantitySymbolValue = product.quantity.symbol.toTextFieldValue(),
            priceValue = product.price.toTextFieldValue(),
            discountValue = discount.toTextFieldValue(),
            totalValue = totalValue,
            noteValue = product.note.toTextFieldValue(),
            discountAsPercent = discount.asPercent,
            discountAsPercentText = discountAsPercentText,
            showDiscountAsPercent = false,
            fontSize = userPreferences.fontSize,
            displayMoney = userPreferences.displayMoney,
            enterToSaveProduct = userPreferences.enterToSaveProduct,
            displayOtherFields = userPreferences.displayOtherFields,
            showNameOtherFields = isDisplayNameOtherFields(),
            showPriceOtherFields = isDisplayPriceOtherFields()
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
            nameValue = autocomplete.name.toTextFieldValue(),
            autocompleteNames = listOf()
        )

        selectedAutocomplete = autocomplete
    }

    fun selectAutocompleteBrand(brand: String) {
        screenData = screenData.copy(
            brandValue = brand.toTextFieldValue(),
            autocompleteBrands = listOf()
        )
    }

    fun selectAutocompleteSize(size: String) {
        screenData = screenData.copy(
            sizeValue = size.toTextFieldValue(),
            autocompleteSizes = listOf()
        )
    }

    fun selectAutocompleteColor(color: String) {
        screenData = screenData.copy(
            colorValue = color.toTextFieldValue(),
            autocompleteColors = listOf()
        )
    }

    fun selectAutocompleteManufacturer(manufacturer: String) {
        screenData = screenData.copy(
            manufacturerValue = manufacturer.toTextFieldValue(),
            autocompleteManufacturers = listOf()
        )
    }

    fun selectAutocompleteQuantity(quantity: Quantity) {
        screenData = screenData.copy(
            quantityValue = quantity.toTextFieldValue(),
            quantitySymbolValue = quantity.symbol.toTextFieldValue(),
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
        screenData = screenData.copy(
            quantitySymbolValue = quantity.symbol.toTextFieldValue(),
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf()
        )
    }

    fun selectAutocompletePrice(price: Money) {
        screenData = screenData.copy(
            priceValue = price.toTextFieldValue(),
            autocompletePrices = listOf()
        )

        when (screenData.lockProductElement) {
            LockProductElement.QUANTITY -> setProductQuantityLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun selectAutocompleteDiscount(discount: Money) {
        val discountAsPercentText: UiText = if (discount.asPercent) {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        }
        screenData = screenData.copy(
            discountValue = discount.toTextFieldValue(),
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
        screenData = screenData.copy(
            totalValue = total.toTextFieldValue(),
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
        val value = screenData.quantityValue.toFloatOrZero().plus(1)
        val quantityValue = Quantity(value = value).toTextFieldValue()
        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun minusOneQuantity() {
        val value = screenData.quantityValue.toFloatOrZero().minus(1)
        val quantity = Quantity(value = value)
        val quantityValue = if (quantity.isEmpty()) {
            "".toTextFieldValue()
        } else {
            quantity.toTextFieldValue()
        }

        screenData = screenData.copy(quantityValue = quantityValue)

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

    fun getCurrentProduct(): Product {
        val product = productWithConfig.product
        return product.copy(
            productUid = screenData.uidValue.text.trim(),
            lastModified = DateTime.getCurrentDateTime(),
            name = screenData.nameValue.text.trim(),
            quantity = product.quantity.copy(
                value = screenData.quantityValue.toFloatOrZero(),
                symbol = screenData.quantitySymbolValue.text.trim()
            ),
            price = product.price.copy(
                value = screenData.priceValue.toFloatOrZero()
            ),
            discount = product.discount.copy(
                value = screenData.discountValue.toFloatOrZero(),
                asPercent = screenData.discountAsPercent
            ),
            total = product.total.copy(
                value = screenData.totalValue.toFloatOrZero()
            ),
            totalFormatted = screenData.lockProductElement != LockProductElement.TOTAL,
            note = screenData.noteValue.text.trim(),
            manufacturer = screenData.manufacturerValue.text.trim(),
            brand = screenData.brandValue.text.trim(),
            size = screenData.sizeValue.text.trim(),
            color = screenData.colorValue.text.trim()
        )
    }

    fun getAutocomplete(): Autocomplete {
        val product = getCurrentProduct()
        val namesEquals = (selectedAutocomplete?.name?.lowercase() ?: "") == product.name.lowercase()
        val personal = (if (namesEquals) selectedAutocomplete?.personal else null) ?: true

        return Autocomplete(
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            total = product.total,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            personal = personal
        )
    }

    fun getUserPreferences(): UserPreferences {
        return productWithConfig.appConfig.userPreferences
    }

    private fun setProductQuantityLock() {
        val price = screenData.priceValue.toFloatOrZero()
        val total = screenData.totalValue.toFloatOrZero()
        val quantity = if (price <= 0f || total <= 0f) {
            "".toTextFieldValue()
        } else {
            Quantity(
                value = total / price,
                decimalFormat = productWithConfig.appConfig.userPreferences.quantityDecimalFormat
            ).toTextFieldValue()
        }

        screenData = screenData.copy(quantityValue = quantity)
    }

    private fun setProductPriceLock() {
        val quantity = screenData.quantityValue.toFloatOrZero()
        val total = screenData.totalValue.toFloatOrZero()
        val price = if (quantity <= 0f || total < 0f) {
            "".toTextFieldValue()
        } else {
            Money(
                value = total / quantity,
                currency = productWithConfig.appConfig.userPreferences.currency,
                asPercent = false,
                decimalFormat = productWithConfig.appConfig.userPreferences.moneyDecimalFormat
            ).toTextFieldValue()
        }

        screenData = screenData.copy(priceValue = price)
    }

    private fun setProductTotalLock() {
        val quantity = screenData.quantityValue.toFloatOrZero()
        val price = screenData.priceValue.toFloatOrZero()
        val total = if (quantity <= 0f || price <= 0f) {
            "".toTextFieldValue()
        } else {
            val totalValue = quantity * price
            val moneyDiscount = Money(
                value = screenData.discountValue.toFloatOrZero(),
                asPercent = screenData.discountAsPercent
            )
            val taxRate = productWithConfig.appConfig.userPreferences.taxRate
            val totalWithDiscountAndTaxRate = totalValue - moneyDiscount.calculateValueFromPercent(totalValue) +
                    taxRate.calculateValueFromPercent(totalValue)
            Money(
                value = totalWithDiscountAndTaxRate,
                currency = productWithConfig.appConfig.userPreferences.currency,
                asPercent = false,
                decimalFormat = productWithConfig.appConfig.userPreferences.moneyDecimalFormat
            ).toTextFieldValue()
        }

        screenData = screenData.copy(totalValue = total)
    }

    private fun isDisplayNameOtherFields(): Boolean {
        val product = productWithConfig.product
        val isFieldsNotEmpty = product.brand.isNotEmpty() ||
                product.size.isNotEmpty() ||
                product.color.isNotEmpty() ||
                product.manufacturer.isNotEmpty()
        return productWithConfig.appConfig.userPreferences.displayOtherFields && isFieldsNotEmpty
    }

    private fun isDisplayPriceOtherFields(): Boolean {
        return productWithConfig.product.discount.isNotEmpty()
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