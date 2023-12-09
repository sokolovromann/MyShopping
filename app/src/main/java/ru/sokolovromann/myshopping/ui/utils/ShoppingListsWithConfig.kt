package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.compose.state.toUiTextOrNothing

fun ShoppingListsWithConfig.getAllShoppingListItems(
    displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
): List<ShoppingListItem> {
    return getSortedShoppingLists(displayCompleted).map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getActivePinnedShoppingListItems(
    displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
): List<ShoppingListItem> {
    return getPinnedOtherSortedShoppingLists(displayCompleted).first.map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getOtherShoppingListItems(
    displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
): List<ShoppingListItem> {
    return getPinnedOtherSortedShoppingLists(displayCompleted).second.map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getTotalText(): UiText {
    return shoppingTotalToText(
        getTotal(),
        false,
        getUserPreferences().displayTotal
    )
}

fun ShoppingListsWithConfig.getSelectedTotal(uids: List<String>): UiText {
    val total = calculateTotalByUids(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

private fun ShoppingListsWithConfig.toShoppingListItems(shoppingList: ShoppingList): ShoppingListItem {
    val displayShoppingsProducts = getUserPreferences().displayShoppingsProducts
    val displayMoney = getUserPreferences().displayMoney
    val displayTotal = getUserPreferences().displayTotal == DisplayTotal.ALL

    val name = shoppingList.shopping.name
    val displayUnnamed = displayShoppingsProducts == DisplayProducts.HIDE && name.isEmpty()
    val nameText: UiText = if (displayUnnamed) {
        UiText.FromResources(R.string.shoppingLists_text_nameNotFound)
    } else {
        UiText.FromString(name)
    }

    val maxShoppingProducts = 10
    val totalFormatted = shoppingList.shopping.totalFormatted && displayTotal
    val productsList = if (shoppingList.products.isEmpty()) {
        val pair = Pair(null, UiText.FromResources(R.string.purchases_text_productsNotFound))
        listOf(pair)
    } else {
        val products: MutableList<Pair<Boolean?, UiText>> = shoppingList.products
            .sortedByDescending { it.pinned }
            .filterIndexed { index, _ -> index < maxShoppingProducts }
            .map {
                productsToPair(
                    product = it,
                    totalFormatted = totalFormatted
                )
            }.toMutableList()

        if (shoppingList.products.size > maxShoppingProducts) {
            val moreProducts = Pair(null, UiText.FromResources(R.string.purchases_text_moreProducts))
            products.add(moreProducts)
        }

        products.toList()
    }
    val totalText: UiText = if (displayMoney) {
        shoppingTotalToText(
            shoppingList.shopping.total,
            shoppingList.shopping.totalFormatted,
            getUserPreferences().displayTotal
        )
    } else {
        UiText.Nothing
    }

    val reminderText: UiText = if (shoppingList.shopping.reminder == null) {
        UiText.Nothing
    } else {
        shoppingList.shopping.reminder.toCalendar().getDisplayDateAndTimeText()
    }

    return ShoppingListItem(
        uid = shoppingList.shopping.uid,
        nameText = nameText,
        productsList = productsList,
        totalText = totalText,
        reminderText = reminderText,
        completed = shoppingList.isCompleted()
    )
}

private fun ShoppingListsWithConfig.productsToPair(
    product: Product,
    totalFormatted: Boolean
): Pair<Boolean, UiText> {
    val multiColumns = getUserPreferences().shoppingsMultiColumns
    val smartphoneScreen = getDeviceConfig().getDeviceSize().isSmartphoneScreen()
    val separator = getUserPreferences().purchasesSeparator
    val displayMoney = getUserPreferences().displayMoney
    val displayOtherFields = getUserPreferences().displayOtherFields

    val builder = StringBuilder(product.name)

    val shortText = multiColumns && smartphoneScreen
    val displayVertically = getUserPreferences().displayShoppingsProducts == DisplayProducts.VERTICAL
    val displayProductElements = !shortText && displayVertically

    if (displayProductElements) {
        val displayBrand = displayOtherFields && product.brand.isNotEmpty()
        if (displayBrand) {
            builder.append(" ")
            builder.append(product.brand)
        }

        val displayQuantity = product.quantity.isNotEmpty()
        val displayPrice = displayMoney && !totalFormatted && product.total.isNotEmpty()

        if (displayPrice) {
            builder.append(separator)
            if (displayQuantity) {
                builder.append(product.quantity)
                builder.append(separator)
            }
            builder.append(product.total)
        } else {
            if (displayQuantity) {
                builder.append(separator)
                builder.append(product.quantity)
            }
        }

        val displayNote = product.note.isNotEmpty()
        if (displayNote) {
            builder.append(separator)
            builder.append(product.note)
        }
    }

    return Pair(
        first = product.completed,
        second = builder.toString().toUiTextOrNothing()
    )
}

private fun shoppingTotalToText(total: Money, totalFormatted: Boolean, displayTotal: DisplayTotal): UiText {
    return if (totalFormatted) {
        UiText.FromResourcesWithArgs(R.string.shoppingLists_text_totalFormatted, total.getDisplayValue())
    } else {
        val id = when (displayTotal) {
            DisplayTotal.ALL -> R.string.shoppingLists_text_allTotal
            DisplayTotal.COMPLETED -> R.string.shoppingLists_text_completedTotal
            DisplayTotal.ACTIVE -> R.string.shoppingLists_text_activeTotal
        }
        UiText.FromResourcesWithArgs(id, total.getDisplayValue())
    }
}