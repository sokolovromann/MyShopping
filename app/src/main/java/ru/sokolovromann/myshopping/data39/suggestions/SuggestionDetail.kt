package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams
import ru.sokolovromann.myshopping.utils.math.DiscountType

sealed class SuggestionDetail {

    data class Image(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    data class Manufacturer(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    data class Brand(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    data class Size(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    data class Color(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    data class Quantity(val value: SuggestionDetailValue<DecimalWithParams<String>>) : SuggestionDetail()

    data class UnitPrice(val value: SuggestionDetailValue<Decimal>) : SuggestionDetail()

    data class Discount(val value: SuggestionDetailValue<DecimalWithParams<DiscountType>>) : SuggestionDetail()

    data class TaxRate(val value: SuggestionDetailValue<Decimal>) : SuggestionDetail()

    data class Cost(val value: SuggestionDetailValue<Decimal>) : SuggestionDetail()

    data class Text(val value: SuggestionDetailValue<String>) : SuggestionDetail()

    fun getClassName(): String {
        return when (this) {
            is Image -> "Image"
            is Manufacturer -> "Manufacturer"
            is Brand -> "Brand"
            is Size -> "Size"
            is Color -> "Color"
            is Quantity -> "Quantity"
            is UnitPrice -> "UnitPrice"
            is Discount -> "Discount"
            is TaxRate -> "TaxRate"
            is Cost -> "Cost"
            is Text -> "Text"
        }
    }

    fun getUid(): UID {
        return when (this) {
            is Image -> value.uid
            is Manufacturer -> value.uid
            is Brand -> value.uid
            is Size -> value.uid
            is Color -> value.uid
            is Quantity -> value.uid
            is UnitPrice -> value.uid
            is Discount -> value.uid
            is TaxRate -> value.uid
            is Cost -> value.uid
            is Text -> value.uid
        }
    }

    fun getDirectory(): UID {
        return when (this) {
            is Image -> value.directory
            is Manufacturer -> value.directory
            is Brand -> value.directory
            is Size -> value.directory
            is Color -> value.directory
            is Quantity -> value.directory
            is UnitPrice -> value.directory
            is Discount -> value.directory
            is TaxRate -> value.directory
            is Cost -> value.directory
            is Text -> value.directory
        }
    }

    fun getCreated(): DateTime {
        return when (this) {
            is Image -> value.created
            is Manufacturer -> value.created
            is Brand -> value.created
            is Size -> value.created
            is Color -> value.created
            is Quantity -> value.created
            is UnitPrice -> value.created
            is Discount -> value.created
            is TaxRate -> value.created
            is Cost -> value.created
            is Text -> value.created
        }
    }

    fun getLastModified(): DateTime {
        return when (this) {
            is Image -> value.lastModified
            is Manufacturer -> value.lastModified
            is Brand -> value.lastModified
            is Size -> value.lastModified
            is Color -> value.lastModified
            is Quantity -> value.lastModified
            is UnitPrice -> value.lastModified
            is Discount -> value.lastModified
            is TaxRate -> value.lastModified
            is Cost -> value.lastModified
            is Text -> value.lastModified
        }
    }

    fun getUsed(): Int {
        return when (this) {
            is Image -> value.used
            is Manufacturer -> value.used
            is Brand -> value.used
            is Size -> value.used
            is Color -> value.used
            is Quantity -> value.used
            is UnitPrice -> value.used
            is Discount -> value.used
            is TaxRate -> value.used
            is Cost -> value.used
            is Text -> value.used
        }
    }
}