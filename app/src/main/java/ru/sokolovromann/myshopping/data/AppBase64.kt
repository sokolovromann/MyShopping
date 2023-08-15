package ru.sokolovromann.myshopping.data

import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AppBase64 @Inject constructor() {

    @OptIn(ExperimentalEncodingApi::class)
    fun encode(value: String): String {
        return Base64.encode(value.toByteArray())
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decode(value: String): String {
        return Base64.decode(value).decodeToString()
    }
}