package ru.sokolovromann.myshopping.data.repository.model

import ru.sokolovromann.myshopping.data.exception.InvalidNameException
import ru.sokolovromann.myshopping.data.exception.InvalidUidException

data class AddEditProduct(
    private val product: Product? = null,
    private val productsLastPosition: Int? = null,
    private val appConfig: AppConfig = AppConfig()
) {

    private val _product = product ?: Product()
    private val userPreferences = appConfig.userPreferences

    fun createProduct(
        productUid: String?,
        name: String?,
        quantity: Float?,
        quantitySymbol: String?,
        price: Float?,
        discount: Float?,
        discountAsPercent: Boolean?,
        total: Float?,
        totalFormatted: Boolean?,
        note: String?,
        manufacturer: String?,
        brand: String?,
        size: String?,
        color: String?
    ): Result<Product> {
        return if (name.isNullOrEmpty()) {
            val exception = InvalidNameException("Name must not be null or empty")
            Result.failure(exception)
        } else {
            val trimProductUid = productUid?.trim()
            if (trimProductUid.isNullOrEmpty()) {
                val exception = InvalidUidException("Uid must not be null or empty")
                Result.failure(exception)
            } else {
                val success = _product.copy(
                    productUid = trimProductUid,
                    position = nextProductsLastPosition(),
                    lastModified = System.currentTimeMillis(),
                    name = name.trim(),
                    quantity = _product.quantity.copy(
                        value = quantity ?: _product.quantity.value,
                        symbol = quantitySymbol?.trim() ?: _product.quantity.symbol
                    ),
                    price = _product.price.copy(
                        value = price ?: _product.price.value
                    ),
                    discount = _product.discount.copy(
                        value = discount ?: _product.discount.value,
                        asPercent = discountAsPercent ?: _product.discount.asPercent
                    ),
                    total = _product.total.copy(
                        value = total ?: _product.total.value
                    ),
                    totalFormatted = totalFormatted ?: _product.totalFormatted,
                    note = note?.trim() ?: _product.note,
                    manufacturer = manufacturer?.trim() ?: _product.manufacturer,
                    brand = brand?.trim() ?: _product.brand,
                    size = size?.trim() ?: _product.size,
                    color = color?.trim() ?: _product.color
                )
                Result.success(success)
            }
        }
    }

    fun createAutocomplete(
        selectedName: String?,
        selectedPersonal: Boolean?,
        productName: String?,
        quantity: Float?,
        quantitySymbol: String?,
        price: Float?,
        discount: Float?,
        discountAsPercent: Boolean?,
        total: Float?,
        manufacturer: String?,
        brand: String?,
        size: String?,
        color: String?
    ): Result<Autocomplete> {
        return if (userPreferences.saveProductToAutocompletes) {
            val exception = UnsupportedOperationException("Disabled saving product")
            Result.failure(exception)
        } else {
            val trimProductName = productName?.trim()
            val namesEquals = (selectedName?.lowercase() ?: "") == trimProductName?.lowercase()
            val personal = (if (namesEquals) selectedPersonal else null) ?: true

            val success = Autocomplete(
                name = trimProductName ?: "",
                quantity = _product.quantity.copy(
                    value = quantity ?: _product.quantity.value,
                    symbol = quantitySymbol?.trim() ?: _product.quantity.symbol
                ),
                price = _product.price.copy(
                    value = price ?: _product.price.value
                ),
                discount = _product.discount.copy(
                    value = discount ?: _product.discount.value,
                    asPercent = discountAsPercent ?: _product.discount.asPercent
                ),
                total = _product.total.copy(
                    value = total ?: _product.total.value
                ),
                manufacturer = manufacturer?.trim() ?: _product.manufacturer,
                brand = brand?.trim() ?: _product.brand,
                size = size?.trim() ?: _product.size,
                color = color?.trim() ?: _product.color,
                personal = personal
            )

            Result.success(success)
        }
    }

    fun getFieldName(): String {
        return _product.name
    }

    fun getSearchName(): String {
        return _product.name.trim()
    }

    fun getFieldUid(): String {
        return _product.productUid
    }

    fun getFieldBrand(): String {
        return _product.brand
    }

    fun getFieldSize(): String {
        return _product.size
    }

    fun getFieldColor(): String {
        return _product.color
    }

    fun getFieldManufacturer(): String {
        return _product.manufacturer
    }

    fun getFieldQuantity(): String {
        val quantity = _product.quantity
        return if (quantity.isEmpty()) "" else quantity.getFormattedValueWithoutSeparators()
    }

    fun calculateFieldQuantity(price: Float?, total: Float?): String {
        return if (price == null || price <= 0f || total == null || total <= 0f) {
            ""
        } else {
            Quantity(value = total / price).getFormattedValueWithoutSeparators()
        }
    }

    fun plusFieldQuantity(quantity: Float?, plus: Int): String {
        val value = (quantity ?: 0f).plus(plus)
        return Quantity(value = value).getFormattedValueWithoutSeparators()
    }

    fun minusFieldQuantity(quantity: Float?, minus: Int): String {
        val value = (quantity ?: 0f).minus(minus)
        val model = Quantity(value = value)
        return if (model.isEmpty()) "" else model.getFormattedValueWithoutSeparators()
    }

    fun getFieldQuantitySymbol(): String {
        return _product.quantity.symbol
    }

    fun getFieldPrice(): String {
        val price = _product.price
        return if (price.isEmpty()) "" else price.getFormattedValueWithoutSeparators()
    }

    fun calculateFieldPrice(quantity: Float?, total: Float?): String {
        return if (quantity == null || quantity <= 0f || total == null || total < 0f) {
            ""
        } else {
            Money(value = total / quantity).getFormattedValueWithoutSeparators()
        }
    }

    fun getFieldDiscount(): String {
        val discount = _product.discount
        return if (discount.isEmpty()) "" else discount.getFormattedValueWithoutSeparators()
    }

    fun isDiscountAsPercent(): Boolean {
        return _product.discount.asPercent
    }

    fun getFieldTotal(): String {
        val total = _product.total
        return if (total.isEmpty()) "" else total.getFormattedValueWithoutSeparators()
    }

    fun calculateFieldTotal(
        quantity: Float?,
        price: Float?,
        discount: Float?,
        discountAsPercent: Boolean?
    ): String {
        return if (quantity == null || quantity <= 0f || price == null || price <= 0f) {
            ""
        } else {
            val totalValue = quantity * price
            val moneyDiscount = Money(
                value = discount ?: 0f,
                asPercent = discountAsPercent ?: false
            )
            val totalWithDiscountAndTaxRate = totalValue - moneyDiscount.calculateValueFromPercent(totalValue) +
                    userPreferences.taxRate.calculateValueFromPercent(totalValue)
            Money(value = totalWithDiscountAndTaxRate).getFormattedValueWithoutSeparators()
        }
    }

    fun getFieldNote(): String {
        return _product.note
    }

    fun getLockProductElement(): LockProductElement {
        return userPreferences.lockProductElement
    }

    fun getFontSize(): FontSize {
        return userPreferences.fontSize
    }

    fun isDisplayMoney(): Boolean {
        return userPreferences.displayMoney
    }

    fun isEnterToSaveProduct(): Boolean {
        return userPreferences.enterToSaveProduct
    }

    fun isDisplayOtherFields(): Boolean {
        return userPreferences.displayOtherFields
    }

    fun isDisplayNameOtherFields(): Boolean {
        val isFieldsNotEmpty = _product.brand.isNotEmpty() ||
                _product.size.isNotEmpty() ||
                _product.color.isNotEmpty() ||
                _product.manufacturer.isNotEmpty()
        return userPreferences.displayOtherFields && isFieldsNotEmpty
    }

    fun isDisplayPriceOtherFields(): Boolean {
        return _product.discount.isNotEmpty()
    }

    private fun isNewProduct(): Boolean {
        return product == null
    }

    private fun nextProductsLastPosition(): Int {
        return if (isNewProduct()) {
            productsLastPosition?.plus(1) ?: 0
        } else {
            _product.position
        }
    }
}