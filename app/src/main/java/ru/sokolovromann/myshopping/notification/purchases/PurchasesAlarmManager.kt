package ru.sokolovromann.myshopping.notification.purchases

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import ru.sokolovromann.myshopping.app.AppAction
import javax.inject.Inject

class PurchasesAlarmManager @Inject constructor(
    private val context: Context
) {

    private val alarmManager: AlarmManager = context
        .getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun createReminder(uid: String, reminder: Long) {
        val pendingIntent = toPendingIntent(uid)
        alarmManager.cancel(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder, pendingIntent)
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminder, pendingIntent)
        }
    }

    fun deleteReminder(uid: String) {
        val pendingIntent = toPendingIntent(uid)
        alarmManager.cancel(pendingIntent)
    }

    fun deleteReminders(uids: List<String>) {
        uids.forEach { deleteReminder(it) }
    }

    fun deleteCodeVersion14Reminder(id: Int) {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, id, Intent(), flags)
        alarmManager.cancel(pendingIntent)
    }

    fun checkCorrectReminderPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val checkSelfPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            checkSelfPermission == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun toPendingIntent(uid: String): PendingIntent {
        val requestCode = 0
        val intent: Intent = Intent(context, PurchasesBroadcastReceiver::class.java).apply {
            action = AppAction.createNotificationsOpenProducts(uid)
            val args = Bundle().apply { putString(PurchasesWorker.UID_KEY, uid) }
            putExtras(args)
        }
        val flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}