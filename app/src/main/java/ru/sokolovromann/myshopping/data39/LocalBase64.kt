package ru.sokolovromann.myshopping.data39

import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject
import kotlin.io.encoding.Base64

class LocalBase64 @Inject constructor() {

    suspend fun encode(value: String): String = withIoContext {
        return@withIoContext Base64.encode(value.toByteArray())
    }

    suspend fun decode(value: String): String = withIoContext {
        return@withIoContext Base64.decode(value).decodeToString()
    }
}