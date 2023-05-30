package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.isEmpty
import ru.sokolovromann.myshopping.ui.utils.toFloatOrZero

class AddEditProductState {

    private var addEditProduct by mutableStateOf(AddEditProduct())

    private var selectedAutocomplete: Autocomplete? by mutableStateOf(null)

    var screenData by mutableStateOf(AddEditProductScreenData())
        private set

    fun populate(addEditProduct: AddEditProduct) {
        this.addEditProduct = addEditProduct

        val product = addEditProduct.product ?: Product()
        val preferences = addEditProduct.preferences

        val name = product.name
        val quantity = if (product.quantity.isEmpty()) "" else product.quantity.valueToString()
        val quantitySymbol = product.quantity.symbol
        val price = if (product.price.isEmpty()) "" else product.price.valueToString()
        val discount = if (product.discount.isEmpty()) "" else product.discount.valueToString()
        val discountAsPercent = product.discount.asPercent
        val discountAsPercentText: UiText = if (discountAsPercent) {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        } else {
            UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        }
        val total = if (product.total.isEmpty()) "" else product.formatTotal().valueToString()
        val note = product.note
        screenData = AddEditProductScreenData(
            screenState = ScreenState.Showing,
            nameValue = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            showNameError = false,
            quantityValue = TextFieldValue(
                text = quantity,
                selection = TextRange(quantity.length),
                composition = TextRange(quantity.length)
            ),
            lockProductElement = preferences.lockProductElement,
            quantitySymbolValue = TextFieldValue(
                text = quantitySymbol,
                selection = TextRange(quantitySymbol.length),
                composition = TextRange(quantitySymbol.length)
            ),
            priceValue = TextFieldValue(
                text = price,
                selection = TextRange(price.length),
                composition = TextRange(price.length)
            ),
            discountValue = TextFieldValue(
                text = discount,
                selection = TextRange(discount.length),
                composition = TextRange(discount.length)
            ),
            totalValue = TextFieldValue(
                text = total,
                selection = TextRange(total.length),
                composition = TextRange(total.length)
            ),
            noteValue = TextFieldValue(
                text = note,
                selection = TextRange(note.length),
                composition = TextRange(note.length)
            ),
            discountAsPercent = discountAsPercent,
            discountAsPercentText = discountAsPercentText,
            showDiscountAsPercent = false,
            fontSize = preferences.fontSize,
            displayMoney = preferences.displayMoney
        )

        selectedAutocomplete = null
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
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

    fun selectAutocompleteQuantity(quantity: Quantity) {
        val quantityText = quantity.valueToString()
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
        val priceText = price.valueToString()
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

    fun selectAutocompleteDiscount(discount: Discount) {
        val discountText = discount.valueToString()
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
        val totalText = total.valueToString()
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
        val quantity = Quantity(value = screenData.quantityValue.toFloatOrZero() + 1)
        val quantityText = quantity.valueToString()
        val quantityValue = TextFieldValue(
            text = quantityText,
            selection = TextRange(quantityText.length),
            composition = TextRange(quantityText.length)
        )

        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.lockProductElement) {
            LockProductElement.PRICE -> setProductPriceLock()
            LockProductElement.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun minusOneQuantity() {
        val quantity = Quantity(value = screenData.quantityValue.toFloatOrZero() - 1)
        val quantityText = if (quantity.isEmpty()) "" else quantity.valueToString()
        val quantityValue = TextFieldValue(
            text = quantityText,
            selection = TextRange(quantityText.length),
            composition = TextRange(quantityText.length)
        )

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
        quantities: List<Quantity>,
        quantitySymbols: List<Quantity>,
        prices: List<Money>,
        discounts: List<Discount>,
        totals: List<Money>
    ) {
        val showDefaultQuantitySymbols = quantitySymbols.isEmpty() && screenData.quantitySymbolValue.isEmpty()
        screenData = screenData.copy(
            autocompleteQuantities = quantities,
            autocompleteQuantitySymbols = quantitySymbols,
            showDefaultQuantitySymbols = showDefaultQuantitySymbols,
            autocompletePrices = prices,
            autocompleteDiscounts = discounts,
            autocompleteTotals = totals
        )
    }

    fun showDiscountAsPercent() {
        screenData = screenData.copy(showDiscountAsPercent = true)
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
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.nameValue.isEmpty()) {
            screenData = screenData.copy(showNameError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)
            Result.success(getSavableProduct(newProduct))
        }
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        return if (addEditProduct.preferences.saveProductToAutocompletes) {
            Result.success(getSavableAutocomplete())
        } else {
            Result.failure(Exception())
        }
    }

    fun getProductLockResult(): Result<LockProductElement> {
        val success = screenData.lockProductElement
        return Result.success(success)
    }

    private fun getSavableProduct(newProduct: Boolean = true): Product {
        val preferences = addEditProduct.preferences
        val position = if (newProduct) {
            addEditProduct.productsLastPosition?.plus(1)
        } else {
            addEditProduct.product?.position ?: 0
        } ?: 0

        return (addEditProduct.product ?: Product()).copy(
            position = position,
            lastModified = System.currentTimeMillis(),
            name = screenData.nameValue.text.trim(),
            quantity = Quantity(
                value = screenData.quantityValue.toFloatOrZero(),
                symbol = screenData.quantitySymbolValue.text.trim()
            ),
            price = Money(
                value = screenData.priceValue.toFloatOrZero(),
                currency = preferences.currency
            ),
            discount = Discount(
                value = screenData.discountValue.toFloatOrZero(),
                asPercent = screenData.discountAsPercent
            ),
            total = Money(
                value = screenData.totalValue.toFloatOrZero(),
                currency = preferences.currency
            ),
            totalFormatted = screenData.lockProductElement != LockProductElement.TOTAL,
            note = screenData.noteValue.text.trim()
        )
    }

    private fun getSavableAutocomplete(): Autocomplete {
        val product = getSavableProduct()
        val namesEquals = (selectedAutocomplete?.name?.lowercase() ?: "") == product.name.lowercase()
        val personal = if (namesEquals) {
            selectedAutocomplete?.personal ?: true
        } else {
            true
        }
        return Autocomplete(
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            taxRate = product.taxRate,
            total = product.total,
            personal = personal
        )
    }

    private fun setProductQuantityLock() {
        val price = screenData.priceValue.toFloatOrZero()
        val total = screenData.totalValue.toFloatOrZero()
        val calculate = price > 0f && total > 0f

        val text = if (calculate) Quantity(value = total / price).valueToString() else ""
        screenData = screenData.copy(
            quantityValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length),
                composition = TextRange(text.length)
            )
        )
    }

    private fun setProductPriceLock() {
        val quantity = screenData.quantityValue.toFloatOrZero()
        val total = screenData.totalValue.toFloatOrZero()
        val calculate = quantity > 0f && total > 0f

        val text = if (calculate) Money(value = total / quantity).valueToString() else ""
        screenData = screenData.copy(
            priceValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length),
                composition = TextRange(text.length)
            )
        )
    }

    private fun setProductTotalLock() {
        val quantity = screenData.quantityValue.toFloatOrZero()
        val price = screenData.priceValue.toFloatOrZero()
        val discount = Discount(
            value = screenData.discountValue.toFloatOrZero(),
            asPercent = screenData.discountAsPercent
        )
        val taxRate = addEditProduct.preferences.taxRate
        val calculate = quantity > 0f && price > 0f

        val text = if (calculate) {
            val totalValue = quantity * price
            val totalWithDiscountAndTaxRate = totalValue - discount.calculate(totalValue) +
                    taxRate.calculate(totalValue)
            Money(value = totalWithDiscountAndTaxRate).valueToString()
        } else {
            ""
        }

        screenData = screenData.copy(
            totalValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length),
                composition = TextRange(text.length)
            )
        )
    }
}

data class AddEditProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val showNameError: Boolean = false,
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
    val autocompleteQuantities: List<Quantity> = listOf(),
    val autocompleteQuantitySymbols: List<Quantity> = listOf(),
    val showDefaultQuantitySymbols: Boolean = true,
    val autocompletePrices: List<Money> = listOf(),
    val autocompleteDiscounts: List<Discount> = listOf(),
    val autocompleteTotals: List<Money> = listOf(),
    val fontSize: FontSize = FontSize.MEDIUM,
    val displayMoney: Boolean = true
)