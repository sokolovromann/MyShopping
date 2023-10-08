package ru.sokolovromann.myshopping.data.repository.model

import java.util.UUID

object Id {

    const val NO_UID: String = ""
    const val NO_ID: Int = 0
    const val FIRST_POSITION: Int = 0

    fun createUid(): String {
        return UUID.randomUUID().toString()
    }

    fun createLastPositionOrFirst(lastPosition: Int?): Int {
        return lastPosition?.plus(1) ?: FIRST_POSITION
    }
}