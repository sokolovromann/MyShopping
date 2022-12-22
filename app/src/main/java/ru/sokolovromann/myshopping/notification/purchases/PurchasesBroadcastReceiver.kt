package ru.sokolovromann.myshopping.notification.purchases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf

class PurchasesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val args = intent?.extras
        if (context != null && args != null) {
            startWork(context, args)
        }
    }

    private fun startWork(context: Context, args: Bundle) {
        val uid = args.getString(PurchasesWorker.UID_KEY)
        val data = workDataOf(PurchasesWorker.UID_KEY to uid)

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PurchasesWorker>()
            .setInputData(data)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(workRequest)
    }
}