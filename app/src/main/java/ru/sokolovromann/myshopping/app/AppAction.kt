package ru.sokolovromann.myshopping.app

object AppAction {

    const val SHORTCUTS_ADD_SHOPPING_LIST: String = "android.intent.action.VIEW"
    const val NOTIFICATIONS_OPEN_PRODUCTS_PREFIX: String = "ru.sokolovromann.myshopping.notification_"
    const val WIDGETS_OPEN_PRODUCTS_PREFIX: String = "ru.sokolovromann.myshopping.products_widget_"

    fun createNotificationsOpenProducts(shoppingUid: String): String {
        return "$NOTIFICATIONS_OPEN_PRODUCTS_PREFIX$shoppingUid"
    }

    fun createWidgetsOpenProducts(shoppingUid: String): String {
        return "$WIDGETS_OPEN_PRODUCTS_PREFIX$shoppingUid"
    }
}