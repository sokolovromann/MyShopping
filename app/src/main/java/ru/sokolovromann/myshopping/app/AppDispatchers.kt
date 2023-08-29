package ru.sokolovromann.myshopping.app

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object AppDispatchers {

    val Default: CoroutineContext = Dispatchers.Default
    val Main: CoroutineContext = Dispatchers.Main
    val IO: CoroutineContext = Dispatchers.IO
}