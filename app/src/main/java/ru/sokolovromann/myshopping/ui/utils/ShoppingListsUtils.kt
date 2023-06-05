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
        val name: UiText = if (preferences.displayShoppingsProducts == DisplayProducts.HIDE && it.name.isEmpty()) {
            UiText.FromResources(R.string.shoppingLists_text_nameNotFound)
        } else {
            UiText.FromString(it.name)
        }

        val totalFormatted = it.totalFormatted && preferences.displayPurchasesTotal == DisplayTotal.ALL

        val productsList = if (it.products.isEmpty()) {
            val pair = Pair(null, UiText.FromResources(R.string.purchases_text_productsNotFound))
            listOf(pair)
        } else {
            val products: MutableList<Pair<Boolean?, UiText>> = it.products
                .filterIndexed { index, _ -> index < defaultProductsLimit}
                .map { product -> productsToPair(product, preferences, totalFormatted) }
                .toMutableList()

            if (it.products.size > defaultProductsLimit) {
                products.add(Pair(null, UiText.FromResources(R.string.purchases_text_moreProducts)))
            }

            products.toList()
        }

        val totalText: UiText = if (preferences.displayMoney) {
            it.calculateTotalToText(totalFormatted)
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
            nameText = name,
            productsList = productsList,
            totalText = totalText,
            reminderText = reminderText,
            completed = it.completed
        )
    }
}

fun ShoppingLists.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), preferences.displayPurchasesTotal, false)
}

fun ShoppingLists.calculateTotalToText(uids: List<String>): UiText {
    val total = calculateTotal(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

private fun ShoppingList.calculateTotalToText(totalFormatted: Boolean): UiText {
    return totalToText(calculateTotal(!totalFormatted), displayTotal, totalFormatted)
}

private fun totalToText(total: Money, displayTotal: DisplayTotal, totalFormatted: Boolean): UiText {
    return if (totalFormatted) {
        UiText.FromResourcesWithArgs(R.string.shoppingLists_text_totalFormatted, total.toString())
    } else {
        val id = when (displayTotal) {
            DisplayTotal.ALL -> R.string.shoppingLists_text_allTotal
            DisplayTotal.COMPLETED -> R.string.shoppingLists_text_completedTotal
            DisplayTotal.ACTIVE -> R.string.shoppingLists_text_activeTotal
        }
        UiText.FromResourcesWithArgs(id, total.toString())
    }
}

private fun productsToPair(
    product: Product,
    preferences: AppPreferences,
    totalFormatted: Boolean
): Pair<Boolean, UiText> {
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted

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
        val str = if (preferences.displayShoppingsProducts == DisplayProducts.COLUMNS) {
            "${product.name}$productsText"
        } else {
            product.name
        }
        UiText.FromString(str)
    }

    return Pair(product.completed, text)
}