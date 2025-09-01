package ru.sokolovromann.myshopping.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DispatcherExtensions {

    suspend fun <T> withContext(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> T
    ): T {
        return withContext(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    fun <T> CoroutineScope.async(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> {
        return async(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    fun CoroutineScope.launch(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    fun <T> Flow<T>.flowOn(dispatcher: Dispatcher): Flow<T> {
        return flowOn(dispatcher.toCoroutineContext())
    }
}