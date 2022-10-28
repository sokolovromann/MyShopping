package ru.sokolovromann.myshopping.notification.purchases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import javax.inject.Inject

class PurchasesAlarmManager @Inject constructor(
    private val context: Context
) {

    private val alarmManager: AlarmManager = context
        .getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun createReminder(uid: String, reminder: Long) {
        val pendingIntent = toPendingIntent(uid)
        alarmManager.cancel(pendingIntent)
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder, pendingIntent)
    }

    fun deleteReminder(uid: String) {
        val pendingIntent = toPendingIntent(uid)
        alarmManager.cancel(pendingIntent)
    }

    fun deleteAppVersion14Reminder(id: Long) {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, id.toInt(), Intent(), flags)
        alarmManager.cancel(pendingIntent)
    }

    private fun toPendingIntent(uid: String): PendingIntent {
        val requestCode = 0
        val intent: Intent = Intent(context, PurchasesBroadcastReceiver::class.java).apply {
            val args = Bundle().apply { putString(PurchasesWorker.UID_KEY, uid) }
            putExtras(args)
        }
        val flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}