package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Products.getProductWidgetItems(): List<ProductWidgetItem> {
    return formatProducts().map {
        val displayQuantity = it.quantity.isNotEmpty()
        val displayPrice = it.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted()

        val body = if (displayPrice) {
            if (displayQuantity) {
                "${it.name} • ${it.quantity} • ${it.formatTotal()}"
            } else {
                "${it.name} • ${it.formatTotal()}"
            }
        } else {
            if (displayQuantity) "${it.name} • ${it.quantity}" else it.name
        }

        ProductWidgetItem(
            uid = it.productUid,
            body = body,
            completed = it.completed
        )
    }
}

fun Products.getProductsItems(
    displayCompleted: DisplayCompleted = preferences.displayCompletedPurchases
): List<ProductItem> {
    return formatProducts(displayCompleted).map {
        val displayQuantity = it.quantity.isNotEmpty()
        val displayPrice = it.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted()

        val brand = if (it.brand.isEmpty()) "" else " ${it.brand}"
        val manufacturer = if (it.manufacturer.isEmpty()) "" else " • ${it.manufacturer}"
        val nameText: UiText = UiText.FromString("${it.name}$brand$manufacturer")

        var otherName = it.size
        otherName += if (it.color.isEmpty()) {
            ""
        } else {
            if (it.size.isEmpty()) it.color else " • ${it.color}"
        }

        var body = otherName
        val otherDivider = if (otherName.isEmpty()) "" else " • "
        body += if (displayPrice) {
            if (displayQuantity) {
                "$otherDivider${it.quantity} • ${it.formatTotal()}"
            } else {
                "$otherDivider${it.formatTotal()}"
            }
        } else {
            if (displayQuantity) "$otherDivider${it.quantity}" else ""
        }

        if (it.note.isNotEmpty()) {
            body += if (body.isEmpty()) it.note else "\n${it.note}"
        }

        val bodyText: UiText = if (body.isEmpty()) UiText.Nothing else UiText.FromString(body)

        ProductItem(
            uid = it.productUid,
            nameText = nameText,
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