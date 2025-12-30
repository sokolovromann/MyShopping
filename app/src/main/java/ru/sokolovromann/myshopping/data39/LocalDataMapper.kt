package ru.sokolovromann.myshopping.data39

import ru.sokolovromann.myshopping.utils.UID
import ru.sokolovromann.myshopping.utils.calendar.DateTime
import ru.sokolovromann.myshopping.utils.math.Decimal
import ru.sokolovromann.myshopping.utils.math.DecimalExtensions.toDecimal
import ru.sokolovromann.myshopping.utils.math.DecimalWithParams

open class LocalDataMapper {

    // ENUM

    inline fun <reified E : Enum<E>> toEnum(name: String?, default: E): E {
        return try {
            val value = name.orEmpty()
            enumValueOf(value)
        } catch (_: Exception) { default }
    }

    inline fun<reified E : Enum<E>> fromEnum(enum: E): String {
        return enum.name
    }

    // UID

    fun toUid(value: String): UID {
        return UID(value)
    }

    fun fromUid(uid: UID): String {
        return uid.value
    }

    fun toUids(values: Collection<String>): Collection<UID> {
        return values.map { toUid(it) }
    }

    fun fromUids(uids: Collection<UID>): List<String> {
        return uids.map { fromUid(it) }.toList()
    }

    // DECIMAL

    fun toDecimal(value: String): Decimal {
        return value.toDecimal()
    }

    fun fromDecimal(decimal: Decimal): String {
        return decimal.toString()
    }

    // DECIMAL WITH PARAMS

    fun<P> toDecimalWithParams(value: String, params: P): DecimalWithParams<P> {
        val decimal = toDecimal(value)
        return DecimalWithParams(decimal, params)
    }

    fun<P> fromDecimalWithParams(decimalWithParams: DecimalWithParams<P>): Pair<String, String> {
        return Pair(
            first = decimalWithParams.decimal.toString(),
            second = decimalWithParams.params.toString()
        )
    }

    // DATE TIME

    fun toDateTime(millis: String): DateTime {
        val emptyDateTime = DateTime.EMPTY
        val longMillis = millis.toLongOrNull() ?: emptyDateTime.getMillis()
        return if (longMillis <= emptyDateTime.getMillis()) {
            emptyDateTime
        } else {
            DateTime(longMillis)
        }
    }

    fun fromDateTime(dateTime: DateTime): String {
        return dateTime.getMillis().toString()
    }
}