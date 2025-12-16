package ru.sokolovromann.myshopping.utils

import java.util.UUID

@JvmInline
value class UID(val value: String) {

    companion object {
        fun createRandom(): UID {
            val value = UUID.randomUUID().toString()
            return UID(value)
        }
    }
}