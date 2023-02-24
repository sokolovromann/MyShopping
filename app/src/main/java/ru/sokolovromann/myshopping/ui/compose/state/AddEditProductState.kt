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

    private var product by mutableStateOf(Product())

    private var productsLastPosition: Int? by mutableStateOf(null)

    private var preferences by mutableStateOf(ProductPreferences())

    private var selectedAutocomplete: Autocomplete? by mutableStateOf(null)

    var screenData by mutableStateOf(AddEditProductScreenData())
        private set

    fun populate(addEditProduct: AddEditProduct) {
        productsLastPosition = addEditProduct.productsLastPosition
        preferences = addEditProduct.preferences
        product = addEditProduct.product ?: Product(
            taxRate = preferences.taxRate
        )

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
            productLock = preferences.productLock,
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
            autocompleteNames = listOf(),
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf(),
            autocompletePrices = listOf(),
            autocompleteDiscounts = listOf(),
            fontSize = preferences.fontSize
        )

        selectedAutocomplete = null
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        product = product.copy(name = nameValue.text.trim())

        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun changeQuantityValue(quantityValue: TextFieldValue) {
        val quantity = product.quantity.copy(value = quantityValue.toFloatOrZero())
        product = product.copy(quantity = quantity)

        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.productLock) {
            ProductLock.PRICE -> setProductPriceLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun changeQuantitySymbolValue(quantitySymbolValue: TextFieldValue) {
        val quantity = product.quantity.copy(symbol = quantitySymbolValue.text.trim())
        product = product.copy(quantity = quantity)

        screenData = screenData.copy(quantitySymbolValue = quantitySymbolValue)
    }

    fun changePriceValue(priceValue: TextFieldValue) {
        val price = product.price.copy(value = priceValue.toFloatOrZero())
        product = product.copy(price = price)

        screenData = screenData.copy(priceValue = priceValue)

        when (screenData.productLock) {
            ProductLock.QUANTITY -> setProductQuantityLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun changeDiscountValue(discountValue: TextFieldValue) {
        val discount = product.discount.copy(value = discountValue.toFloatOrZero())
        product = product.copy(discount = discount)

        screenData = screenData.copy(discountValue = discountValue)

        if (screenData.productLock == ProductLock.TOTAL) {
            setProductTotalLock()
        }
    }

    fun changeProductTotalValue(totalValue: TextFieldValue) {
        val total = product.total.copy(value = totalValue.toFloatOrZero())
        product = product.copy(total = total)

        screenData = screenData.copy(totalValue = totalValue)

        when (screenData.productLock) {
            ProductLock.QUANTITY -> setProductQuantityLock()
            ProductLock.PRICE -> setProductPriceLock()
            else -> {}
        }
    }

    fun changeProductNoteValue(noteValue: TextFieldValue) {
        product = product.copy(note = noteValue.text.trim())
        screenData = screenData.copy(noteValue = noteValue)
    }

    fun selectAutocompleteName(autocomplete: Autocomplete) {
        product = product.copy(name = autocomplete.name)

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
        product = product.copy(quantity = quantity)

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

        when (screenData.productLock) {
            ProductLock.PRICE -> setProductPriceLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun selectAutocompleteQuantitySymbol(quantity: Quantity) {
        product = product.copy(quantity = quantity)

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
        product = product.copy(price = price)

        val priceText = price.valueToString()
        screenData = screenData.copy(
            priceValue = TextFieldValue(
                text = priceText,
                selection = TextRange(priceText.length),
                composition = TextRange(priceText.length)
            ),
            autocompletePrices = listOf()
        )

        when (screenData.productLock) {
            ProductLock.QUANTITY -> setProductQuantityLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun selectAutocompleteDiscount(discount: Discount) {
        product = product.copy(discount = discount)

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

        if (screenData.productLock == ProductLock.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectDiscountAsPercent() {
        val discount = product.discount.copy(asPercent = true)
        product = product.copy(discount = discount)

        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        screenData = screenData.copy(
            discountAsPercent = true,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )

        if (screenData.productLock == ProductLock.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectDiscountAsMoney() {
        val discount = product.discount.copy(asPercent = false)
        product = product.copy(discount = discount)

        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        screenData = screenData.copy(
            discountAsPercent = false,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )

        if (screenData.productLock == ProductLock.TOTAL) {
            setProductTotalLock()
        }
    }

    fun selectProductLock(productLock: ProductLock) {
        product = product.copy(totalFormatted = productLock != ProductLock.TOTAL)
        screenData = screenData.copy(
            productLock = productLock,
            showProductLock = false
        )
    }

    fun plusOneQuantity() {
        val quantity = product.quantity.copy(value = product.quantity.value + 1)
        product = product.copy(quantity = quantity)

        val quantityText = quantity.valueToString()
        val quantityValue = TextFieldValue(
            text = quantityText,
            selection = TextRange(quantityText.length),
            composition = TextRange(quantityText.length)
        )

        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.productLock) {
            ProductLock.PRICE -> setProductPriceLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun minusOneQuantity() {
        val quantity = product.quantity.copy(value = product.quantity.value - 1)
        product = product.copy(quantity = quantity)

        val quantityText = if (quantity.isEmpty()) "" else quantity.valueToString()
        val quantityValue = TextFieldValue(
            text = quantityText,
            selection = TextRange(quantityText.length),
            composition = TextRange(quantityText.length)
        )

        screenData = screenData.copy(quantityValue = quantityValue)

        when (screenData.productLock) {
            ProductLock.PRICE -> setProductPriceLock()
            ProductLock.TOTAL -> setProductTotalLock()
            else -> {}
        }
    }

    fun showAutocompleteNames(autocompleteNames: List<Autocomplete>) {
        val names = if (preferences.displayDefaultAutocomplete) {
            autocompleteNames
        } else {
            autocompleteNames.filter { !it.default }
        }
        screenData = screenData.copy(autocompleteNames = names)
    }

    fun showProducts(
        quantities: List<Quantity>,
        quantitySymbols: List<Quantity>,
        prices: List<Money>,
        discounts: List<Discount>
    ) {
        screenData = screenData.copy(
            autocompleteQuantities = quantities,
            autocompleteQuantitySymbols = quantitySymbols,
            autocompletePrices = prices,
            autocompleteDiscounts = discounts
        )
    }

    fun showDiscountAsPercent() {
        screenData = screenData.copy(showDiscountAsPercent = true)
    }

    fun showProductLock() {
        screenData = screenData.copy(showProductLock = true)
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
            autocompletePrices = listOf(),
            autocompleteDiscounts = listOf()
        )
    }

    fun hideProducts() {
        screenData = screenData.copy(
            autocompleteQuantities = listOf(),
            autocompleteQuantitySymbols = listOf(),
            autocompletePrices = listOf(),
            autocompleteDiscounts = listOf()
        )
    }

    fun hideDiscountAsPercent() {
        screenData = screenData.copy(showDiscountAsPercent = false)
    }

    fun hideProductLock() {
        screenData = screenData.copy(showProductLock = false)
    }

    fun getDisplayAutocomplete(): DisplayAutocomplete {
        return preferences.displayAutocomplete
    }

    fun getProductResult(newProduct: Boolean): Result<Product> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.nameValue.isEmpty()) {
            screenData = screenData.copy(showNameError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)

            val position = if (newProduct) {
                productsLastPosition?.plus(1) ?: 0
            } else {
                product.position
            }
            val success = product.copy(
                position = position,
                totalFormatted = screenData.productLock != ProductLock.TOTAL,
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        return if (preferences.addLastProduct) {
            if (selectedAutocomplete == null) {
                val success = Autocomplete(name = screenData.nameValue.text.trim())
                Result.success(success)
            } else {
                Result.failure(Exception())
            }
        } else {
            Result.failure(Exception())
        }
    }

    fun getProductLockResult(): Result<ProductLock> {
        val success = screenData.productLock
        return Result.success(success)
    }

    private fun setProductQuantityLock() {
        val quantity: Quantity
        val text: String
        if (product.price.isEmpty() || product.total.isEmpty()) {
            quantity = product.quantity.copy(value = 0f)
            text = ""
        } else {
            val quantityValue = product.total.value / product.price.value
            quantity = product.quantity.copy(value = quantityValue)
            text = quantity.valueToString()
        }

        product = product.copy(quantity = quantity)
        screenData = screenData.copy(
            quantityValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length),
                composition = TextRange(text.length)
            )
        )
    }

    private fun setProductPriceLock() {
        val price: Money
        val text: String

        if (product.quantity.isEmpty() || product.total.isEmpty()) {
            price = product.price.copy(value = 0f)
            text = ""
        } else {
            val priceValue = product.total.value / product.quantity.value
            price = product.price.copy(value = priceValue)
            text = price.valueToString()
        }

        product = product.copy(price = price)
        screenData = screenData.copy(
            priceValue = TextFieldValue(
                text = text,
                selection = TextRange(text.length),
                composition = TextRange(text.length)
            )
        )
    }

    private fun setProductTotalLock() {
        val total: Money
        val text: String

        if (product.quantity.isEmpty() || product.price.isEmpty()) {
            total = product.total.copy(value = 0f)
            text = ""
        } else {
            val totalValue = product.quantity.value * product.price.value
            val totalWithDiscountAndTaxRate = totalValue -
                    product.discountToMoney().value + preferences.taxRate.calculate(totalValue)

            total = product.total.copy(value = totalWithDiscountAndTaxRate)
            text = total.valueToString()
        }

        product = product.copy(total = total)
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
    val productLock: ProductLock = ProductLock.DefaultValue,
    val showProductLock: Boolean = false,
    val noteValue: TextFieldValue = TextFieldValue(),
    val autocompleteNames: List<Autocomplete> = listOf(),
    val autocompleteQuantities: List<Quantity> = listOf(),
    val autocompleteQuantitySymbols: List<Quantity> = listOf(),
    val autocompletePrices: List<Money> = listOf(),
    val autocompleteDiscounts: List<Discount> = listOf(),
    val fontSize: FontSize = FontSize.MEDIUM
)