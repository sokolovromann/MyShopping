package ru.sokolovromann.myshopping.core.domain.model

import java.util.UUID

@JvmInline
value class UID(val value: String) {

    init {
        require(value.isNotEmpty()) {
            "The value must not be empty."
        }
    }

    companion object {
        fun createRandom(): UID = UID(UUID.randomUUID().toString())
    }
}