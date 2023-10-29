package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.compose.state.toUiTextOrNothing

fun ShoppingListsWithConfig.getAllShoppingListItems(): List<ShoppingListItem> {
    val shoppingLists = getPinnedOtherShoppingLists().first.toMutableList()
        .apply {
            addAll(getPinnedOtherShoppingLists().second)
        }
    return shoppingLists.map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getActivePinnedShoppingListItems(): List<ShoppingListItem> {
    return getPinnedOtherShoppingLists().first.map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getOtherShoppingListItems(): List<ShoppingListItem> {
    return getPinnedOtherShoppingLists().first.map { toShoppingListItems(it) }
}

fun ShoppingListsWithConfig.getTotalText(): UiText {
    return shoppingTotalToText(
        getTotal(),
        false,
        appConfig.userPreferences.displayTotal
    )
}

fun ShoppingListsWithConfig.getSelectedTotal(uids: List<String>): UiText {
    val total = calculateTotalByUids(uids)
    return UiText.FromResourcesWithArgs(R.string.shoppingLists_text_selectedTotal, total.toString())
}

private fun ShoppingListsWithConfig.getPinnedOtherShoppingLists(): Pair<List<ShoppingList>, List<ShoppingList>> {
    return shoppingLists.partition { !it.isCompleted() && it.shopping.pinned }
}

private fun ShoppingListsWithConfig.toShoppingListItems(shoppingList: ShoppingList): ShoppingListItem {
    val displayShoppingsProducts = appConfig.userPreferences.displayShoppingsProducts
    val displayMoney = appConfig.userPreferences.displayMoney
    val displayTotal = appConfig.userPreferences.displayTotal == DisplayTotal.ALL

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
            appConfig.userPreferences.displayTotal
        )
    } else {
        UiText.Nothing
    }

    val reminderText: UiText = if (shoppingList.shopping.reminder == null) {
        UiText.Nothing
    } else {
        shoppingList.shopping.reminder.toCalendar().getDisplayDateAndTime()
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
    val multiColumns = appConfig.userPreferences.shoppingsMultiColumns
    val smartphoneScreen = appConfig.deviceConfig.getDeviceSize().isSmartphoneScreen()
    val separator = appConfig.userPreferences.purchasesSeparator
    val displayMoney = appConfig.userPreferences.displayMoney
    val displayOtherFields = appConfig.userPreferences.displayOtherFields

    val builder = StringBuilder(product.name)

    val shortText = multiColumns && smartphoneScreen
    val displayVertically = appConfig.userPreferences.displayShoppingsProducts == DisplayProducts.VERTICAL
    val displayProductElements = !shortText && displayVertically

    if (displayProductElements) {
        val displayBrand = displayOtherFields && product.brand.isNotEmpty()
        if (displayBrand) {
            builder.append(separator)
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