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
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository

class BootPurchasesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootPurchasesWorkerEntryPoint {
        fun repository(): ShoppingListsRepository
        fun dispatchers(): AppDispatchers
        fun alarmManager(): PurchasesAlarmManager
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        BootPurchasesWorkerEntryPoint::class.java
    )

    override suspend fun doWork(): Result {
        return getShoppingListsWithConfig().let {
            if (it == null) {
                Result.failure()
            } else {
                createReminders(it)
                Result.success()
            }
        }
    }

    private suspend fun getShoppingListsWithConfig(): ShoppingListsWithConfig? {
        return entryPoint.repository().getRemindersWithConfig().firstOrNull()
    }

    private suspend fun createReminders(
        shoppingListsWithConfig: ShoppingListsWithConfig
    ): Unit = withContext(entryPoint.dispatchers().main) {
        shoppingListsWithConfig.shoppingLists.forEach {
            val shopping = it.shopping
            entryPoint.alarmManager().createReminder(
                uid = shopping.uid,
                reminder = shopping.reminder?.millis ?: throw NullPointerException("")
            )
        }
    }
}