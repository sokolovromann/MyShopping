package ru.sokolovromann.myshopping.notification.purchases

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext

class PurchasesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val UID_KEY: String = "UID_KEY"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PurchasesWorkerEntryPoint {
        fun repository(): ShoppingListsRepository
        fun notificationManager(): PurchasesNotificationManager
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        PurchasesWorkerEntryPoint::class.java
    )

    override suspend fun doWork(): Result {
        return getShoppingListWithConfig().let {
            if (it == null) {
                Result.failure()
            } else {
                showNotification(it)
                Result.success()
            }
        }
    }

    private suspend fun getShoppingListWithConfig(): ShoppingListWithConfig? {
        val uid = inputData.getString(UID_KEY) ?: return null
        entryPoint.repository().deleteReminders(listOf(uid))

        return entryPoint.repository().getShoppingListWithConfig(uid).firstOrNull()
    }

    private suspend fun showNotification(
        shoppingListWithConfig: ShoppingListWithConfig
    ): Unit = withContext(Dispatcher.Main) {
        entryPoint.notificationManager().showNotification(shoppingListWithConfig)
    }
}