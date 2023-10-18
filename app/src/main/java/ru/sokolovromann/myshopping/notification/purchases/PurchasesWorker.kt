package ru.sokolovromann.myshopping.notification.purchases

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.model.mapper.ShoppingListsMapper
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.repository.model.ShoppingListNotification

class PurchasesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val UID_KEY: String = "UID_KEY"
        fun createAction(uid: String): String {
            return "ru.sokolovromann.myshopping.notification_$uid"
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PurchasesWorkerEntryPoint {
        fun repository(): ShoppingListsRepository
        fun dispatchers(): AppDispatchers
        fun notificationManager(): PurchasesNotificationManager
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        PurchasesWorkerEntryPoint::class.java
    )

    override suspend fun doWork(): Result {
        return getShoppingList().let {
            if (it == null) {
                Result.failure()
            } else {
                showNotification(it)
                Result.success()
            }
        }
    }

    private suspend fun getShoppingList(): ShoppingListNotification? {
        val uid = inputData.getString(UID_KEY) ?: return null

        entryPoint.repository().deleteReminder(uid)
        val shoppingListWithConfig = entryPoint.repository().getShoppingListWithConfig(uid).firstOrNull()
        return if (shoppingListWithConfig == null) null else ShoppingListsMapper.toShoppingListNotification(shoppingListWithConfig)
    }

    private suspend fun showNotification(
        notification: ShoppingListNotification
    ) = withContext(entryPoint.dispatchers().main) {
        entryPoint.notificationManager().showNotification(notification)
    }
}