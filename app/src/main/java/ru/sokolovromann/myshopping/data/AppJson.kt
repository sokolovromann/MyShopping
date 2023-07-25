package ru.sokolovromann.myshopping.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AppJson @Inject constructor() {

    inline fun <reified T> encodeToString(value: T): String {
        return Json.encodeToString(value)
    }

    inline fun <reified T> decodeFromString(value: String): T {
        return Json.decodeFromString(value)
    }
}