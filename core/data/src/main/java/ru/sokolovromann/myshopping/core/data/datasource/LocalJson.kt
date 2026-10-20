package ru.sokolovromann.myshopping.core.data.datasource

import jakarta.inject.Inject
import kotlinx.serialization.json.Json

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