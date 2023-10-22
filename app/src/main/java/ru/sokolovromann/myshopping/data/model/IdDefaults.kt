package ru.sokolovromann.myshopping.data.model

import java.util.UUID

object IdDefaults {

    const val NO_UID: String = ""
    const val NO_ID: Int = 0
    const val FIRST_POSITION: Int = 0

    fun createUid(): String {
        return UUID.randomUUID().toString()
    }
}