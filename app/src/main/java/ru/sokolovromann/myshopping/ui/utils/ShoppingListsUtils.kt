package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import java.util.*

fun ShoppingList.getShoppingListLocation(): ShoppingListLocation {
    return if (deleted) {
        ShoppingListLocation.TRASH
    } else {
        if (archived) {
            ShoppingListLocation.ARCHIVE
        } else {
            ShoppingListLocation.PURCHASES
        }
    }
}

fun ShoppingLists.getShoppingListItems(
    displayCompleted: DisplayCompleted = preferences.displayCompletedPurchases
): List<ShoppingListItem> {
    val defaultProductsLimit = 10
    return formatShoppingLists(displayCompleted).map {
        val productsList = if (it.products.isEmpty()) {
            val pair = Pair(null, UiText.FromResources(R.string.purchases_text_productsNotFound))
            listOf(pair)
        } else {
            val products: MutableList<Pair<Boolean?, UiText>> = it.products
                .filterIndexed { index, _ -> index < defaultProductsLimit}
                .map { product -> productsToPair(product, preferences) }
                .toMutableList()

            if (it.products.size > defaultProductsLimit) {
                products.add(Pair(null, UiText.FromResources(R.string.purchases_text_moreProducts)))
            }

            products.toList()
        }

        val totalText: UiText = if (preferences.displayMoney) {
            it.calculateTotalToText()
        } else {
            UiText.Nothing
        }

        val reminderText: UiText = if (it.reminder == null) {
            UiText.Nothing
        } else {
            Calendar.getInstance()
                .apply { timeInMillis = it.reminder }
                .getDisplayDateAndTime()
        }

        ShoppingListItem(
            uid = it.uid,
            nameText = UiText.FromString(it.name),
            productsList = productsList,
            totalText = totalText,
            reminderText = reminderText,
            completed = it.completed
        )
    }
}

fun ShoppingLists.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), preferences.displayPurchasesTotal)
}

fun ShoppingLists.calculateTotalToText(uids: List<String>): UiText {
    val total = calculateTotal(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

fun ShoppingList.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), displayTotal)
}

private fun totalToText(total: Money, displayTotal: DisplayTotal): UiText {
    val id = when (displayTotal) {
        DisplayTotal.ALL -> R.string.shoppingLists_text_allTotal
        DisplayTotal.COMPLETED -> R.string.shoppingLists_text_completedTotal
        DisplayTotal.ACTIVE -> R.string.shoppingLists_text_activeTotal
    }
    return UiText.FromResourcesWithArgs(id, total.toString())
}

private fun productsToPair(
    product: Product,
    preferences: AppPreferences
): Pair<Boolean, UiText> {
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.formatTotal().isNotEmpty() && preferences.displayMoney

    var productsText = if (displayPrice) {
        if (displayQuantity) {
            " • ${product.quantity} • ${product.formatTotal()}"
        } else {
            " • ${product.formatTotal()}"
        }
    } else {
        if (displayQuantity) " • ${product.quantity}" else ""
    }

    if (product.note.isNotEmpty()) {
        productsText += " • ${product.note}"
    }

    val shortText = preferences.shoppingsMultiColumns && preferences.smartphoneScreen

    val text: UiText = if (shortText) {
        UiText.FromString(product.name)
    } else {
        val str = "${product.name}$productsText"
        UiText.FromString(str)
    }

    return Pair(product.completed, text)
}