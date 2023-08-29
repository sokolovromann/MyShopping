package ru.sokolovromann.myshopping

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Deprecated("Use app/AppDispatchers")
class AppDispatchers @Inject constructor() {
    val default: CoroutineContext = Dispatchers.Default
    val main: CoroutineContext = Dispatchers.Main
    val io: CoroutineContext = Dispatchers.IO
    val unconfined: CoroutineContext = Dispatchers.Unconfined
}