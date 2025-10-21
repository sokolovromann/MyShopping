package ru.sokolovromann.myshopping.utils

import java.util.UUID

object UID {

    fun createRandom(): String {
        return UUID.randomUUID().toString()
    }
}