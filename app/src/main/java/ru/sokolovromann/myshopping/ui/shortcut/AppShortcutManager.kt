package ru.sokolovromann.myshopping.ui.shortcut

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.ui.UiRouteKey
import ru.sokolovromann.myshopping.ui.activity.MainActivity
import javax.inject.Inject

class AppShortcutManager @Inject constructor(
    private val context: Context
) {

    fun updateShoppingListsShortcuts(shoppingLists: List<ShoppingList>) {
        shoppingLists.forEachIndexed { index, shoppingList ->
            if (!shoppingList.isProductsEmpty()) {
                val shoppingUid = shoppingList.shopping.uid
                val label = shoppingList.shopping.name.ifEmpty {
                    val builder = StringBuilder()

                    val maxCount = 2
                    val sortedProducts = shoppingList.getSortedProducts(DisplayCompleted.LAST)
                    val products = sortedProducts.filterIndexed { index, _ -> index < maxCount }
                    builder.append(products.joinToString { it.name })

                    if (sortedProducts.size > maxCount) {
                        builder.append("...")
                    }

                    builder.toString()
                }
                val iconResId = if (shoppingList.isCompleted()) {
                    R.drawable.ic_shortcut_completed_shopping_list
                } else {
                    R.drawable.ic_shortcut_active_shopping_list
                }
                val icon = IconCompat.createWithResource(context, iconResId)
                val intent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    val args = Bundle().apply { putExtra(UiRouteKey.ShoppingUid.key, shoppingUid) }
                    putExtras(args)
                }

                val shortcut = ShortcutInfoCompat.Builder(context, shoppingUid)
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(icon)
                    .setIntent(intent)
                    .setRank(index)
                    .build()

                ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
            }
        }
    }

    fun removeAllShoppingListsShortcuts() {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }
}