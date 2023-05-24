package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Products.getProductsItems(
    displayCompleted: DisplayCompleted = preferences.displayCompletedPurchases
): List<ProductItem> {
    return formatProducts(displayCompleted).map {
        val displayQuantity = it.quantity.isNotEmpty()
        val displayPrice = it.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted()

        var body = if (displayPrice) {
            if (displayQuantity) {
                "${it.quantity} • ${it.formatTotal()}"
            } else {
                "${it.formatTotal()}"
            }
        } else {
            if (displayQuantity) "${it.quantity}" else ""
        }

        if (it.note.isNotEmpty()) {
            body += if (body.isEmpty()) it.note else "\n${it.note}"
        }

        val bodyText: UiText = if (body.isEmpty()) UiText.Nothing else UiText.FromString(body)

        ProductItem(
            uid = it.productUid,
            nameText = UiText.FromString(it.name),
            bodyText = bodyText,
            completed = it.completed
        )
    }
}

fun Products.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), preferences.displayPurchasesTotal, totalFormatted())
}

fun Products.calculateTotalToText(uids: List<String>): UiText {
    val total = calculateTotal(uids)
    return UiText.FromResourcesWithArgs(R.string.products_text_selectedTotal, total.toString())
}

private fun totalToText(total: Money, displayTotal: DisplayTotal, totalFormatted: Boolean): UiText {
    return if (totalFormatted) {
        UiText.FromResourcesWithArgs(R.string.products_text_totalFormatted, total.toString())
    } else {
        val id = when (displayTotal) {
            DisplayTotal.ALL -> R.string.products_text_allTotal
            DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
            DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
        }
        UiText.FromResourcesWithArgs(id, total.toString())
    }
}