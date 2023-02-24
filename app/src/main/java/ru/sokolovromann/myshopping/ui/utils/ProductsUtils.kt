package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Products.getProductsItems(): List<ProductItem> {
    return formatProducts().map {
        val displayQuantity = it.quantity.isNotEmpty()
        val displayPrice = it.price.isNotEmpty() && preferences.displayMoney

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
    return totalToText(calculateTotal(), preferences.displayTotal)
}

private fun totalToText(total: Money, displayTotal: DisplayTotal): UiText {
    val id = when (displayTotal) {
        DisplayTotal.ALL -> R.string.products_text_allTotal
        DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
        DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
    }
    return UiText.FromResourcesWithArgs(id, total.toString())
}