package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import java.util.*

fun ShoppingLocation.toShoppingListLocation(): ShoppingListLocation {
    return when (this) {
        ShoppingLocation.PURCHASES -> ShoppingListLocation.PURCHASES
        ShoppingLocation.ARCHIVE -> ShoppingListLocation.ARCHIVE
        ShoppingLocation.TRASH -> ShoppingListLocation.TRASH
    }
}

fun ShoppingLists.getAllShoppingListItems(): List<ShoppingListItem> {
    return getAllShoppingLists().map { toShoppingListItems(it) }
}

fun ShoppingLists.getActivePinnedShoppingListItems(): List<ShoppingListItem> {
    return getActivePinnedShoppingLists().map { toShoppingListItems(it) }
}

fun ShoppingLists.getOtherShoppingListItems(): List<ShoppingListItem> {
    return getOtherShoppingLists().map { toShoppingListItems(it) }
}

fun ShoppingLists.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), getDisplayTotal(), false)
}

fun ShoppingLists.calculateTotalToText(uids: List<String>): UiText {
    val total = calculateTotal(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

private fun ShoppingList.calculateTotalToText(totalFormatted: Boolean): UiText {
    return totalToText(calculateTotal(!totalFormatted), displayTotal, totalFormatted)
}

private fun ShoppingLists.toShoppingListItems(shoppingList: ShoppingList): ShoppingListItem {
    val name = shoppingListToName(shoppingList)
    val nameText: UiText = if (name == ShoppingLists.UNNAMED) {
        UiText.FromResources(R.string.shoppingLists_text_nameNotFound)
    } else {
        UiText.FromString(name)
    }

    val products = shoppingListToProducts(shoppingList)
    val productsList = if (products.isEmpty()) {
        val pair = Pair(null, UiText.FromResources(R.string.purchases_text_productsNotFound))
        listOf(pair)
    } else {
        products.map {
            val second: UiText = if (it.second == ShoppingLists.MAX_PRODUCTS_SIGN) {
                UiText.FromResources(R.string.purchases_text_moreProducts)
            } else {
                UiText.FromString(it.second)
            }
            Pair(it.first, second)
        }
    }

    val totalFormatted = shoppingList.totalFormatted && getDisplayTotal() == DisplayTotal.ALL
    val totalText: UiText = if (displayMoney()) {
        shoppingList.calculateTotalToText(totalFormatted)
    } else {
        UiText.Nothing
    }

    val reminderText: UiText = if (shoppingList.reminder == null) {
        UiText.Nothing
    } else {
        Calendar.getInstance()
            .apply { timeInMillis = shoppingList.reminder }
            .getDisplayDateAndTime()
    }

    return ShoppingListItem(
        uid = shoppingList.uid,
        nameText = nameText,
        productsList = productsList,
        totalText = totalText,
        reminderText = reminderText,
        completed = shoppingList.completed
    )
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