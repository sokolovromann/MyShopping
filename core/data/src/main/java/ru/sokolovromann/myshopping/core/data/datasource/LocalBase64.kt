package ru.sokolovromann.myshopping.core.data.datasource

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import kotlin.io.encoding.Base64

class LocalBase64 @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun encode(value: String): String =
        withContext(ioDispatcher) {
            Base64.encode(value.toByteArray())
        }

    suspend fun decode(value: String): String =
        withContext(ioDispatcher) {
            Base64.decode(value).decodeToString()
        }
}