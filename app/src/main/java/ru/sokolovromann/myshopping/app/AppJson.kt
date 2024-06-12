package ru.sokolovromann.myshopping.app

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AppJson {

    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> encodeToString(value: T): String {
        return Json.encodeToString(value)
    }

    inline fun <reified T> decodeFromString(value: String): T {
        return json.decodeFromString(value)
    }
}