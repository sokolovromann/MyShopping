package ru.sokolovromann.myshopping.app

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object AppBase64 {

    @OptIn(ExperimentalEncodingApi::class)
    fun encode(value: String): String {
        return Base64.encode(value.toByteArray())
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decode(value: String): String {
        return Base64.decode(value).decodeToString()
    }
}