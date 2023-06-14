package ru.sokolovromann.myshopping.ui.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.updateIf
import ru.sokolovromann.myshopping.widget.WidgetKey
import ru.sokolovromann.myshopping.widget.products.ProductsWidget

suspend fun updateProductsWidgetState(
    context: Context,
    glanceId: GlanceId,
    shoppingUid: String
) {
    updateAppWidgetState(context.applicationContext, glanceId) {
        it[stringPreferencesKey(WidgetKey.SHOPPING_UID.name)] = shoppingUid
    }
}

suspend fun updateProductsWidget(
    context: Context,
    shoppingUid: String
) {
    ProductsWidget().updateIf<Preferences>(context.applicationContext) {
        it[stringPreferencesKey(WidgetKey.SHOPPING_UID.name)] == shoppingUid
    }
}

suspend fun updateProductsWidgets(context: Context) {
    ProductsWidget().updateAll(context.applicationContext)
}