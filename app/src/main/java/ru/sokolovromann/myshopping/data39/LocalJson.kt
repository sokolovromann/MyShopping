package ru.sokolovromann.myshopping.data39

import kotlinx.serialization.json.Json
import javax.inject.Inject

class LocalJson @Inject constructor() {

    inline fun <reified T> encodeToString(value: T): String {
        return Json.encodeToString(value)
    }

    inline fun <reified T> decodeFromString(value: String): T {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString(value)
    }
}