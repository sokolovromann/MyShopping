package ru.sokolovromann.myshopping.notification.purchases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootPurchasesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            startWork(context)
        }
    }

    private fun startWork(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<BootPurchasesWorker>().build()
        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }
}