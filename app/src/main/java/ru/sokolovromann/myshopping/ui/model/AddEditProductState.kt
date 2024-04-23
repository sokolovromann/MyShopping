package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.DateTime
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

    private var productWithConfig: ProductWithConfig by mutableStateOf(ProductWithConfig())

    var waiting: Boolean by mutableStateOf(true)
        private set

    var nameValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var nameError: Boolean by mutableStateOf(false)
        private set

    var uidValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var uidError: Boolean by mutableStateOf(false)
        private set

    var brandValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var sizeValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var colorValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var manufacturerValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var quantityValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var quantitySymbolValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var priceValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var discountValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var discountAsPercentValue: SelectedValue<Boolean> by mutableStateOf(SelectedValue(false))
        private set

    var expandedDiscountAsPercent: Boolean by mutableStateOf(false)
        private set

    var totalValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var lockProductElementValue: SelectedValue<LockProductElement> by mutableStateOf(SelectedValue(LockProductElement.DefaultValue))
        private set

    var expandedLockProductElement: Boolean by mutableStateOf(false)
        private set

    var noteValue: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var autocompletes: AutocompletesSelectedValue by mutableStateOf(AutocompletesSelectedValue())
        private set

    var displayMoney: Boolean by mutableStateOf(true)
        private set

    var enterToSaveProduct: Boolean by mutableStateOf(true)
        private set

    var displayOtherFields: Boolean by mutableStateOf(false)
        private set

    var displayNameOtherFields: Boolean by mutableStateOf(false)
        private set

    var displayPriceOtherFields: Boolean by mutableStateOf(false)
        private set

    fun populate(productWithConfig: ProductWithConfig) {
        this.productWithConfig = productWithConfig

        val product = productWithConfig.product
        val userPreferences = productWithConfig.appConfig.userPreferences

        waiting = false
        nameValue = product.name.toTextFieldValue()
        nameError = false
        uidValue = product.productUid.toTextFieldValue()
        uidError = false
        brandValue = product.brand.toTextFieldValue()
        sizeValue = product.size.toTextFieldValue()
        colorValue = product.color.toTextFieldValue()
        manufacturerValue = product.manufacturer.toTextFieldValue()
        quantityValue = product.quantity.toTextFieldValue()
        quantitySymbolValue = product.quantity.symbol.toTextFieldValue()
        priceValue = product.price.toTextFieldValue()
        discountValue = product.discount.toTextFieldValue()
        discountAsPercentValue = toDiscountSelectedValue(product.discount.asPercent)
        expandedDiscountAsPercent = false
        totalValue = if (product.quantity.isEmpty() && product.price.isNotEmpty()) {
            "".toTextFieldValue()
        } else {
            product.total.toTextFieldValue()
        }
        lockProductElementValue = toLockProductElementSelectedValue(userPreferences.lockProductElement)
        expandedLockProductElement = false
        noteValue = product.note.toTextFieldValue()
        autocompletes = AutocompletesSelectedValue()
        displayMoney = userPreferences.displayMoney
        enterToSaveProduct = userPreferences.enterToSaveProduct
        displayOtherFields = userPreferences.displayOtherFields

        val isFieldsNotEmpty = product.brand.isNotEmpty() ||
                product.size.isNotEmpty() ||
                product.color.isNotEmpty() ||
                product.manufacturer.isNotEmpty()
        displayNameOtherFields = userPreferences.displayOtherFields && isFieldsNotEmpty

        displayPriceOtherFields = product.discount.isNotEmpty()
    }

    fun onNameValueChanged(value: TextFieldValue) {
        nameValue = value
        nameError = false
        waiting = false
    }

    fun onNameSelected(autocomplete: Autocomplete) {
        nameValue = autocomplete.name.toTextFieldValue()
        nameError = false
        waiting = false
        autocompletes = autocompletes.copy(
            names = listOf(),
            selected = autocomplete
        )
    }

    fun onInvalidNameValue() {
        nameError = true
        waiting = false
    }

    fun onInvertNameOtherFields() {
        val displayFields = !displayNameOtherFields
        displayNameOtherFields = displayFields
    }

    fun onUidValueChanged(value: TextFieldValue) {
        uidValue = value
        uidError = false
        waiting = false
    }

    fun onInvalidUidValue() {
        uidError = false
        waiting = false
    }

    fun onBrandValueChanged(value: TextFieldValue) {
        brandValue = value
    }

    fun onBrandSelected(brand: String) {
        brandValue = brand.toTextFieldValue()
        autocompletes = autocompletes.copy(brands = listOf())
    }

    fun onSizeValueChanged(value: TextFieldValue) {
        sizeValue = value
    }

    fun onSizeSelected(size: String) {
        sizeValue = size.toTextFieldValue()
        autocompletes = autocompletes.copy(sizes = listOf())
    }

    fun onColorValueChanged(value: TextFieldValue) {
        colorValue = value
    }

    fun onColorSelected(color: String) {
        colorValue = color.toTextFieldValue()
        autocompletes = autocompletes.copy(colors = listOf())
    }

    fun onManufacturerValueChanged(value: TextFieldValue) {
        manufacturerValue = value
    }

    fun onManufacturerSelected(manufacturer: String) {
        manufacturerValue = manufacturer.toTextFieldValue()
        autocompletes = autocompletes.copy(manufacturers = listOf())
    }

    fun onQuantityValueChanged(value: TextFieldValue) {
        quantityValue = value
    }

    fun onQuantitySelected(quantity: Quantity) {
        quantityValue = quantity.toTextFieldValue()
        quantitySymbolValue = quantity.symbol.toTextFieldValue()
        autocompletes = autocompletes.copy(
            quantities = listOf(),
            quantitySymbols = listOf(),
            displayDefaultQuantitySymbols = false
        )
    }

    fun onQuantitySymbolValueChanged(value: TextFieldValue) {
        quantitySymbolValue = value
        autocompletes = autocompletes.copy(displayDefaultQuantitySymbols = false)
    }

    fun onQuantitySymbolSelected(quantity: Quantity) {
        quantitySymbolValue = quantity.symbol.toTextFieldValue()
        autocompletes = autocompletes.copy(
            quantities = listOf(),
            quantitySymbols = listOf(),
            displayDefaultQuantitySymbols = false
        )
    }

    fun onPriceValueChanged(value: TextFieldValue) {
        priceValue = value
    }

    fun onPriceSelected(price: Money) {
        priceValue = price.toTextFieldValue()
        autocompletes = autocompletes.copy(prices = listOf())
    }

    fun onInvertPriceOtherFields() {
        val displayFields = !displayPriceOtherFields
        displayPriceOtherFields = displayFields
    }

    fun onDiscountValueChanged(value: TextFieldValue) {
        discountValue = value
    }

    fun onDiscountSelected(discount: Money) {
        discountValue = discount.toTextFieldValue()
        discountAsPercentValue = toDiscountSelectedValue(discount.asPercent)
        autocompletes = autocompletes.copy(discounts = listOf())
    }

    fun onDiscountAsPercentSelected(asPercent: Boolean) {
        discountAsPercentValue = toDiscountSelectedValue(asPercent)
        expandedDiscountAsPercent = false
    }

    fun onSelectDiscountAsPercent(expanded: Boolean) {
        expandedDiscountAsPercent = expanded
    }

    fun onTotalValueChanged(value: TextFieldValue) {
        totalValue = value
    }

    fun onTotalSelected(total: Money) {
        totalValue = total.toTextFieldValue()
        autocompletes = autocompletes.copy(totals = listOf())
    }

    fun onNoteValueChanged(value: TextFieldValue) {
        noteValue = value
    }

    fun onLockProductElementSelected(lockProductElement: LockProductElement) {
        lockProductElementValue = toLockProductElementSelectedValue(lockProductElement)
        expandedLockProductElement = false
    }

    fun onSelectLockProductElement(expanded: Boolean) {
        expandedLockProductElement = expanded
    }

    fun onWaiting() {
        waiting = true
    }

    fun onShowAutocomplete(value: AutocompletesSelectedValue) {
        autocompletes = value
    }

    fun onHideAutocompletes() {
        autocompletes = AutocompletesSelectedValue(
            displayDefaultQuantitySymbols = quantitySymbolValue.isEmpty(),
            selected = autocompletes.selected
        )
    }

    fun getCurrentUserPreferences(): UserPreferences {
        return productWithConfig.appConfig.userPreferences
    }

    fun getCurrentProduct(): Product {
        val product = productWithConfig.product
        return product.copy(
            productUid = uidValue.text.trim(),
            lastModified = DateTime.getCurrentDateTime(),
            name = nameValue.text.trim(),
            quantity = product.quantity.copy(
                value = quantityValue.toFloatOrZero(),
                symbol = quantitySymbolValue.text.trim()
            ),
            price = product.price.copy(
                value = priceValue.toFloatOrZero()
            ),
            discount = product.discount.copy(
                value = discountValue.toFloatOrZero(),
                asPercent = discountAsPercentValue.selected
            ),
            total = product.total.copy(
                value = totalValue.toFloatOrZero()
            ),
            totalFormatted = lockProductElementValue.selected != LockProductElement.TOTAL,
            note = noteValue.text.trim(),
            manufacturer = manufacturerValue.text.trim(),
            brand = brandValue.text.trim(),
            size = sizeValue.text.trim(),
            color = colorValue.text.trim()
        )
    }

    fun getCurrentAutocomplete(): Autocomplete {
        val product = getCurrentProduct()
        val selectedAutocomplete = autocompletes.selected

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

    private fun toDiscountSelectedValue(asPercent: Boolean): SelectedValue<Boolean> {
        return SelectedValue(
            selected = asPercent,
            text = if (asPercent) {
                UiString.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
            } else {
                UiString.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
            }
        )
    }

    private fun toLockProductElementSelectedValue(
        lockProductElement: LockProductElement
    ): SelectedValue<LockProductElement> {
        return SelectedValue(
            selected = lockProductElement,
            text = when (lockProductElement) {
                LockProductElement.QUANTITY -> UiString.FromResources(R.string.addEditProduct_action_selectProductLockQuantity)
                LockProductElement.PRICE -> UiString.FromResources(R.string.addEditProduct_action_selectProductLockPrice)
                LockProductElement.TOTAL -> UiString.FromResources(R.string.addEditProduct_action_selectProductLockTotal)
            }
        )
    }
}