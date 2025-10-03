package ru.sokolovromann.myshopping.io

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject
import kotlin.io.encoding.Base64

class LocalBase64 @Inject constructor(
    private val dispatcher: Dispatcher
) {

    suspend fun encode(value: String): String = withContext(dispatcher) {
        return@withContext Base64.encode(value.toByteArray())
    }

    suspend fun decode(value: String): String = withContext(dispatcher) {
        return@withContext Base64.decode(value).decodeToString()
    }
}