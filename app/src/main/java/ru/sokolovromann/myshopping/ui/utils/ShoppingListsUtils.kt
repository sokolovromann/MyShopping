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

fun ShoppingLists.getAllShoppingListItems(
    splitByPinned: Boolean = true,
    displayCompleted: DisplayCompleted = appConfig.userPreferences.displayCompleted
): List<ShoppingListItem> {
    return getAllShoppingLists(splitByPinned, displayCompleted).map { toShoppingListItems(it) }
}

fun ShoppingLists.getActivePinnedShoppingListItems(): List<ShoppingListItem> {
    return getActivePinnedShoppingLists().map { toShoppingListItems(it) }
}

fun ShoppingLists.getOtherShoppingListItems(
    displayCompleted: DisplayCompleted = appConfig.userPreferences.displayCompleted
): List<ShoppingListItem> {
    return getOtherShoppingLists(displayCompleted).map { toShoppingListItems(it) }
}

fun ShoppingLists.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), appConfig.userPreferences.displayTotal, false)
}

fun ShoppingLists.calculateTotalToText(uids: List<String>): UiText {
    val total = calculateTotal(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

private fun ShoppingList.calculateTotalToText(totalFormatted: Boolean): UiText {
    return totalToText(calculateTotal(!totalFormatted), displayTotal, totalFormatted)
}

private fun ShoppingLists.toShoppingListItems(shoppingList: ShoppingList): ShoppingListItem {
    val defaultProductsLimit = 10
    val name: UiText = if (appConfig.userPreferences.displayShoppingsProducts == DisplayProducts.HIDE && shoppingList.name.isEmpty()) {
        UiText.FromResources(R.string.shoppingLists_text_nameNotFound)
    } else {
        UiText.FromString(shoppingList.name)
    }

    val totalFormatted = shoppingList.totalFormatted && appConfig.userPreferences.displayTotal == DisplayTotal.ALL

    val productsList = if (shoppingList.products.isEmpty()) {
        val pair = Pair(null, UiText.FromResources(R.string.purchases_text_productsNotFound))
        listOf(pair)
    } else {
        val products: MutableList<Pair<Boolean?, UiText>> = shoppingList.products
            .filterIndexed { index, _ -> index < defaultProductsLimit}
            .map { product -> productsToPair(product, appConfig, totalFormatted) }
            .toMutableList()

        if (shoppingList.products.size > defaultProductsLimit) {
            products.add(Pair(null, UiText.FromResources(R.string.purchases_text_moreProducts)))
        }

        products.toList()
    }

    val totalText: UiText = if (appConfig.userPreferences.displayMoney) {
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
        nameText = name,
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

private fun productsToPair(
    product: Product,
    appConfig: AppConfig,
    totalFormatted: Boolean
): Pair<Boolean, UiText> {
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.formatTotal().isNotEmpty() && appConfig.userPreferences.displayMoney && !totalFormatted

    var productsText = if (product.brand.isEmpty() || !appConfig.userPreferences.displayOtherFields) "" else " ${product.brand}"
    productsText += if (displayPrice) {
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

    val shortText = appConfig.userPreferences.shoppingsMultiColumns && appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium

    val text: UiText = if (shortText) {
        UiText.FromString(product.name)
    } else {
        val str = if (appConfig.userPreferences.displayShoppingsProducts == DisplayProducts.COLUMNS) {
            "${product.name}$productsText"
        } else {
            product.name
        }
        UiText.FromString(str)
    }

    return Pair(product.completed, text)
}