package ru.sokolovromann.myshopping.utils

object EnumExtensions {

    inline fun <reified T : Enum<T>> valueOfOrNull(name: String?): T? {
        return try {
            val value = name.orEmpty()
            enumValueOf<T>(value)
        } catch (_: Exception) { null }
    }

    inline fun <reified T : Enum<T>> valueOfOrDefault(name: String?, defaultValue: T): T {
        return valueOfOrNull<T>(name) ?: defaultValue
    }
}