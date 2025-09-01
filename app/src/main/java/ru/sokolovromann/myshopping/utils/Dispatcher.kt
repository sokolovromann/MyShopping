package ru.sokolovromann.myshopping.utils

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

enum class Dispatcher {

    Main,

    IO;

    fun toCoroutineContext(): CoroutineContext {
        return when (this) {
            Main -> Dispatchers.Main
            IO -> Dispatchers.IO
        }
    }
}