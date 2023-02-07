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

    private var productNameFromAutocompletes by mutableStateOf(false)

    var screenData by mutableStateOf(AddEditProductScreenData())
        private set

    fun populate(addEditProduct: AddEditProduct) {
        product = addEditProduct.product ?: Product()
        productsLastPosition = addEditProduct.productsLastPosition
        preferences = addEditProduct.preferences

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
            lockQuantity = preferences.lockQuantity,
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

        productNameFromAutocompletes = false
    }

    fun changeNameValue(nameValue: TextFieldValue) {
        screenData = screenData.copy(
            nameValue = nameValue,
            showNameError = false
        )
    }

    fun changeQuantityValue(quantityValue: TextFieldValue) {
        screenData = screenData.copy(quantityValue = quantityValue)
    }

    fun changeQuantitySymbolValue(quantitySymbolValue: TextFieldValue) {
        screenData = screenData.copy(quantitySymbolValue = quantitySymbolValue)
    }

    fun changePriceValue(priceValue: TextFieldValue) {
        screenData = screenData.copy(priceValue = priceValue)
    }

    fun changeDiscountValue(discountValue: TextFieldValue) {
        screenData = screenData.copy(discountValue = discountValue)
    }

    fun selectAutocompleteName(name: String) {
        screenData = screenData.copy(
            nameValue = TextFieldValue(
                text = name,
                selection = TextRange(name.length),
                composition = TextRange(name.length)
            ),
            autocompleteNames = listOf()
        )

        productNameFromAutocompletes = true
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
    }

    fun selectDiscountAsPercent() {
        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsPercents)
        screenData = screenData.copy(
            discountAsPercent = true,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )
    }

    fun selectDiscountAsMoney() {
        val asPercentText: UiText = UiText.FromResources(R.string.addEditProduct_action_selectDiscountAsMoney)
        screenData = screenData.copy(
            discountAsPercent = false,
            discountAsPercentText = asPercentText,
            showDiscountAsPercent = false
        )
    }

    fun plusOneQuantity() {
        val quantity = Quantity(
            value = screenData.quantityValue.toFloatOrZero() + 1f
        ).valueToString()

        val quantityValue = TextFieldValue(
            text = quantity,
            selection = TextRange(quantity.length),
            composition = TextRange(quantity.length)
        )

        screenData = screenData.copy(quantityValue = quantityValue)
    }

    fun minusOneQuantity() {
        val quantity = Quantity(
            value = screenData.quantityValue.toFloatOrZero() - 1f
        )
        val quantityText = if (quantity.isEmpty()) "" else quantity.valueToString()

        val quantityValue = TextFieldValue(
            text = quantityText,
            selection = TextRange(quantityText.length),
            composition = TextRange(quantityText.length)
        )

        screenData = screenData.copy(quantityValue = quantityValue)
    }

    fun showAutocompleteNames(autocompleteNames: List<String>) {
        screenData = screenData.copy(autocompleteNames = autocompleteNames)
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

    fun hideAutocompleteNames() {
        screenData = screenData.copy(
            autocompleteNames = listOf(),
        )
        productNameFromAutocompletes = true
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

    fun getDisplayAutocomplete(): DisplayAutocomplete {
        return preferences.displayAutocomplete
    }

    fun getProductResult(): Result<Product> {
        screenData = screenData.copy(screenState = ScreenState.Saving)

        return if (screenData.nameValue.isEmpty()) {
            screenData = screenData.copy(showNameError = true)
            Result.failure(Exception())
        } else {
            screenData = screenData.copy(screenState = ScreenState.Saving)

            val position = productsLastPosition?.plus(1) ?: 0
            val success = product.copy(
                position = position,
                name = screenData.nameValue.text,
                quantity = Quantity(
                    value = screenData.quantityValue.toFloatOrZero(),
                    symbol = screenData.quantitySymbolValue.text
                ),
                price = Money(
                    value = screenData.priceValue.toFloatOrZero(),
                    currency = preferences.currency,
                ),
                discount = Discount(
                    value = screenData.discountValue.toFloatOrZero(),
                    asPercent = screenData.discountAsPercent
                ),
                taxRate = preferences.taxRate,
                lastModified = System.currentTimeMillis()
            )
            Result.success(success)
        }
    }

    fun getAutocompleteResult(): Result<Autocomplete> {
        return if (!productNameFromAutocompletes && preferences.addLastProduct) {
            val success = Autocomplete(name = screenData.nameValue.text)
            Result.success(success)
        } else {
            Result.failure(Exception())
        }
    }
}

data class AddEditProductScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val nameValue: TextFieldValue = TextFieldValue(),
    val showNameError: Boolean = false,
    val quantityValue: TextFieldValue = TextFieldValue(),
    val lockQuantity: Boolean = false,
    val quantitySymbolValue: TextFieldValue = TextFieldValue(),
    val priceValue: TextFieldValue = TextFieldValue(),
    val discountValue: TextFieldValue = TextFieldValue(),
    val discountAsPercent: Boolean = true,
    val discountAsPercentText: UiText = UiText.Nothing,
    val showDiscountAsPercent: Boolean = false,
    val autocompleteNames: List<String> = listOf(),
    val autocompleteQuantities: List<Quantity> = listOf(),
    val autocompleteQuantitySymbols: List<Quantity> = listOf(),
    val autocompletePrices: List<Money> = listOf(),
    val autocompleteDiscounts: List<Discount> = listOf(),
    val fontSize: FontSize = FontSize.MEDIUM
)