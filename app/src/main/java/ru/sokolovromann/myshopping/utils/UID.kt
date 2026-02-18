package ru.sokolovromann.myshopping.utils

import java.nio.charset.StandardCharsets
import java.util.UUID

@JvmInline
value class UID(val value: String) {

    companion object {
        fun createRandom(): UID {
            val value = UUID.randomUUID().toString()
            return UID(value)
        }
        fun createFromString(str: String): UID {
            val formatted = str.trim().lowercase()
            val value = UUID.nameUUIDFromBytes(
                formatted.toByteArray(StandardCharsets.UTF_8)
            ).toString()
            return UID(value)
        }
    }
}