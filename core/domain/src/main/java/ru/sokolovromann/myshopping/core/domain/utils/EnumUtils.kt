package ru.sokolovromann.myshopping.core.domain.utils

object EnumUtils {

    inline fun <reified T : Enum<T>> valueOfOrDefault(name: String?, defaultValue: T): T =
        try {
            checkNotNull(name)
            enumValueOf(name)
        } catch (_ : Exception) { defaultValue }
}