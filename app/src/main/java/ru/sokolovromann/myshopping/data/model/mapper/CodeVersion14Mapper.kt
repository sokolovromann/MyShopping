package ru.sokolovromann.myshopping.data.model.mapper

import android.database.Cursor
import ru.sokolovromann.myshopping.data.local.entity.CodeVersion14UserPreferencesEntity
import ru.sokolovromann.myshopping.data.model.Autocomplete
import ru.sokolovromann.myshopping.data.model.CodeVersion14
import ru.sokolovromann.myshopping.data.model.CodeVersion14Preferences
import ru.sokolovromann.myshopping.data.model.Currency
import ru.sokolovromann.myshopping.data.model.DateTime
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.Quantity
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.UserPreferencesDefaults
import ru.sokolovromann.myshopping.data.utils.uppercaseFirst

object CodeVersion14Mapper {

    fun toCodeVersion14(
        shoppingsCursor: Cursor,
        productsCursor: Cursor,
        autocompletesCursor: Cursor,
        defaultAutocompleteNames: List<String>,
        preferences: CodeVersion14UserPreferencesEntity
    ): CodeVersion14 {
        val shoppingLists = mutableListOf<ShoppingList>()
        val autocompletes = mutableListOf<Autocomplete>()
        while (shoppingsCursor.moveToNext()) {
            val shopping = toShopping(shoppingsCursor)

            val products = mutableListOf<Product>()
            while (productsCursor.moveToNext()) {
                val product = toProduct(productsCursor, preferences)
                if (product.shoppingUid == shopping.uid) {
                    products.add(product)

                    if (preferences.saveProductToAutocompletes == true) {
                        val personal = isPersonalAutocomplete(defaultAutocompleteNames, product.name)
                        val autocomplete = toAutocompleteFromProduct(product, personal)
                        autocompletes.add(autocomplete)
                    }
                }
            }

            val shoppingList = ShoppingList(
                shopping = shopping.copy(
                    total = calculateProductsTotal(products, preferences),
                    totalFormatted = false
                ),
                products = products.formatCodeVersion14Products(
                    sort = preferences.sort ?: 0,
                    firstLetterUppercase = preferences.firstLetterUppercase ?: false
                )
            )
            shoppingLists.add(shoppingList)

            productsCursor.moveToFirst()
        }

        while (autocompletesCursor.moveToNext()) {
            val autocompleteFromCursor = toAutocomplete(autocompletesCursor)
            val autocomplete = autocompleteFromCursor.copy(
                personal = isPersonalAutocomplete(defaultAutocompleteNames, autocompleteFromCursor.name)
            )
            autocompletes.add(autocomplete)
        }

        return CodeVersion14(
            shoppingLists = shoppingLists.formatCodeVersion14ShoppingLists(
                sort = preferences.sort ?: 0,
                firstLetterUppercase = preferences.firstLetterUppercase ?: false
            ),
            autocompletes = autocompletes.toList(),
            preferences = toCodeVersion14Preferences(preferences)
        )
    }

    private fun toShopping(cursor: Cursor): Shopping {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("listname"))
        val alarm = cursor.getLong(cursor.getColumnIndexOrThrow("alarm"))

        return Shopping(
            id = id.toInt(),
            position = cursor.position,
            uid = id.toString(),
            name = name,
            reminder = if (alarm == 0L) null else DateTime(alarm)
        )
    }

    private fun toProduct(cursor: Cursor, preferences: CodeVersion14UserPreferencesEntity): Product {
        val listId = cursor.getLong(cursor.getColumnIndexOrThrow("listid"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("goodsname"))
        val number = cursor.getString(cursor.getColumnIndexOrThrow("number"))
        val numberMeasure = cursor.getString(cursor.getColumnIndexOrThrow("numbermeasure"))
        val priceMeasure = cursor.getFloat(cursor.getColumnIndexOrThrow("pricemeasure"))
        val buy = cursor.getInt(cursor.getColumnIndexOrThrow("goodsbuy"))

        val completed = 2130837592

        val quantity = Quantity(
            value = number.toFloatOrNull() ?: 0f,
            symbol = numberMeasure.replace(".", ""),
            decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
        )

        val currency = Currency(
            symbol = preferences.currency ?: "",
            displayToLeft = preferences.displayCurrencyToLeft ?: false
        )
        val price = Money(
            value = priceMeasure,
            currency = currency,
            asPercent = false,
            decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
        )

        return Product(
            position = cursor.position,
            shoppingUid = listId.toString(),
            name = name,
            quantity = quantity,
            price = price,
            taxRate = Money(
                value = preferences.taxRate ?: 0f,
                currency = currency,
                asPercent = true,
                decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
            ),
            total = Money(
                value = quantity.value * price.value,
                currency = currency,
                asPercent = false,
                decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
            ),
            totalFormatted = false,
            completed = buy == completed
        )
    }

    private fun toAutocomplete(cursor: Cursor): Autocomplete {
        val name = cursor.getString(cursor.getColumnIndexOrThrow("completename"))
        return Autocomplete(name = name)
    }

    private fun toAutocompleteFromProduct(product: Product, personal: Boolean): Autocomplete {
        return Autocomplete(
            name = product.name,
            quantity = product.quantity,
            price = product.price,
            discount = product.discount,
            taxRate = product.taxRate,
            total = product.total,
            manufacturer = product.manufacturer,
            brand = product.brand,
            size = product.size,
            color = product.color,
            provider = product.provider,
            personal = personal
        )
    }

    private fun toCodeVersion14Preferences(entity: CodeVersion14UserPreferencesEntity): CodeVersion14Preferences {
        val currency = Currency(
            symbol = entity.currency ?: "",
            displayToLeft = entity.displayCurrencyToLeft ?: false
        )

        return CodeVersion14Preferences(
            firstOpened = entity.firstOpened ?: false,
            currency = currency,
            taxRate = Money(
                value = entity.taxRate ?: 0f,
                currency = currency,
                asPercent = true,
                decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
            ),
            fontSize = toFontSize(entity.titleFontSize),
            multiColumns = toMultiColumns(entity.columnCount),
            displayMoney = entity.displayMoney ?: true,
            displayTotal = toDisplayTotal(entity.displayTotal),
            editProductAfterCompleted = entity.editProductAfterCompleted ?: false,
            saveProductToAutocompletes = entity.saveProductToAutocompletes ?: false
        )
    }

    private fun isPersonalAutocomplete(defaultNames: List<String>, search: String): Boolean {
        return defaultNames.find { it.equals(search, true) } == null
    }

    private fun calculateProductsTotal(
        products: List<Product>,
        userPreferencesEntity: CodeVersion14UserPreferencesEntity
    ): Money {
        var all = 0f
        var completed = 0f
        var active = 0f

        products.forEach {
            val totalValue = it.total.value

            all += totalValue
            if (it.completed) {
                completed += totalValue
            } else {
                active += totalValue
            }
        }

        val total = when (toDisplayTotal(userPreferencesEntity.displayTotal)) {
            DisplayTotal.ALL -> all
            DisplayTotal.COMPLETED -> completed
            DisplayTotal.ACTIVE -> active
        }

        val currency = Currency(
            symbol = userPreferencesEntity.currency ?: "",
            displayToLeft = userPreferencesEntity.displayCurrencyToLeft ?: false
        )

        return Money(
            value = total,
            currency = currency,
            asPercent = false,
            decimalFormat = UserPreferencesDefaults.getMoneyDecimalFormat()
        )
    }

    private fun List<ShoppingList>.formatCodeVersion14ShoppingLists(
        sort: Int,
        firstLetterUppercase: Boolean
    ): List<ShoppingList> {
        return when (sort) {
            1, 3 -> this.sortedBy { it.shopping.name }
            2, 4 -> this.sortedBy { it.shopping.total.value }
            else -> this.sortedBy { it.shopping.id }
        }.withIndex().map {
            val name = it.value.shopping.name
            val shopping = it.value.shopping.copy(
                position = it.index,
                name = if (firstLetterUppercase) name.uppercaseFirst() else name
            )
            it.value.copy(shopping = shopping)
        }
    }

    private fun List<Product>.formatCodeVersion14Products(
        sort: Int,
        firstLetterUppercase: Boolean
    ): List<Product> {
        return when (sort) {
            1, 3 -> this.sortedBy { it.name }
            2, 4 -> this.sortedBy { it.total.value }
            else -> this.sortedBy { it.id }
        }.withIndex().map {
            val name = it.value.name
            it.value.copy(
                position = it.index,
                name = if (firstLetterUppercase) name.uppercaseFirst() else name
            )
        }
    }

    private fun toFontSize(value: Int?): FontSize {
        val fontSize = value ?: 18
        return if (fontSize <= 14) {
            FontSize.SMALL
        } else if (fontSize <= 16) {
            FontSize.MEDIUM
        } else if (fontSize <= 18) {
            FontSize.LARGE
        } else if (fontSize <= 20) {
            FontSize.VERY_LARGE
        } else if (fontSize <= 22) {
            FontSize.HUGE
        } else if (fontSize <= 24) {
            FontSize.VERY_HUGE
        } else {
            FontSize.MEDIUM
        }
    }

    private fun toMultiColumns(columnCount: Int?): Boolean {
        return (columnCount ?: 1) > 1
    }

    private fun toDisplayTotal(value: Int?): DisplayTotal {
        return when (value) {
            0 -> DisplayTotal.ALL
            1 -> DisplayTotal.ACTIVE
            2 -> DisplayTotal.COMPLETED
            else -> DisplayTotal.ALL
        }
    }
}