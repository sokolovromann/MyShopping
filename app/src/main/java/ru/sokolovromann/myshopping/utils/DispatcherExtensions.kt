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

    // WITH CONTEXT

    suspend fun <T> withContext(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> T
    ): T {
        return withContext(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    suspend fun <T> withIoContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatcher.IO, block)
    }

    suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatcher.Main, block)
    }

    // ASYNC

    fun <T> CoroutineScope.async(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> {
        return async(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    fun <T> CoroutineScope.asyncOnIo(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(Dispatcher.IO, block)
    }

    fun <T> CoroutineScope.asyncOnMain(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(Dispatcher.Main, block)
    }

    // LAUNCH

    fun CoroutineScope.launch(
        dispatcher: Dispatcher,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(
            context = dispatcher.toCoroutineContext(),
            block = block
        )
    }

    fun CoroutineScope.launchOnIo(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(Dispatcher.IO, block)
    }

    fun CoroutineScope.launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(Dispatcher.Main, block)
    }

    // FLOW ON

    fun <T> Flow<T>.flowOn(dispatcher: Dispatcher): Flow<T> {
        return flowOn(dispatcher.toCoroutineContext())
    }

    fun <T> Flow<T>.flowOnIo(): Flow<T> {
        return flowOn(Dispatcher.IO)
    }

    fun <T> Flow<T>.flowOnMain(): Flow<T> {
        return flowOn(Dispatcher.Main)
    }
}