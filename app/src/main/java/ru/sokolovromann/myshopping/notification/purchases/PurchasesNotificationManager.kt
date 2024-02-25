package ru.sokolovromann.myshopping.notification.purchases

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.app.AppAction
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.utils.toProductsString
import ru.sokolovromann.myshopping.notification.AppNotificationChannel
import ru.sokolovromann.myshopping.ui.activity.MainActivity
import ru.sokolovromann.myshopping.ui.UiRouteKey
import javax.inject.Inject

class PurchasesNotificationManager @Inject constructor(
    private val context: Context
) {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationName = context.getString(R.string.notificationChannel_name_purchases)
            val notificationDescription = context.getString(R.string.notificationChannel_description_purchases)
            val channel = NotificationChannel(
                AppNotificationChannel.Purchases.name,
                notificationName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = notificationDescription }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(shoppingListWithConfig: ShoppingListWithConfig) {
        val shopping = shoppingListWithConfig.getShopping()
        val pendingIntent: PendingIntent = toPendingIntent(shopping.uid)

        val builder = NotificationCompat.Builder(context, AppNotificationChannel.Purchases.name)
            .setSmallIcon(R.drawable.ic_all_purchases)
            .setContentText(shoppingListWithConfig.getSortedProducts().toProductsString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (shopping.name.isNotEmpty()) {
            builder.setContentTitle(shopping.name)
        }

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val checkSelfPermission = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                    notify(shopping.id, builder.build())
                }
            } else {
                notify(shopping.id, builder.build())
            }
        }
    }

    private fun toPendingIntent(uid: String): PendingIntent {
        val requestCode = 0
        val intent: Intent = Intent(context, MainActivity::class.java).apply {
            action = AppAction.createNotificationsOpenProducts(uid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val extras = Bundle().apply { putString(UiRouteKey.ShoppingUid.key, uid) }
            putExtras(extras)
        }
        val flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
}