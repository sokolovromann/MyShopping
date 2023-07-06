package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.Money
import ru.sokolovromann.myshopping.data.repository.model.Product
import ru.sokolovromann.myshopping.data.repository.model.Products
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Products.getActivePinnedProductWidgetItems(): List<ProductWidgetItem> {
    return getActivePinnedProducts().map { toProductWidgetItem(it) }
}

fun Products.getOtherProductWidgetItems(): List<ProductWidgetItem> {
    return getOtherProducts().map { toProductWidgetItem(it) }
}

fun Products.getActivePinnedProductItems(): List<ProductItem> {
    return getActivePinnedProducts().map { toProductItem(it) }
}

fun Products.getOtherProductItems(
    displayCompleted: DisplayCompleted = preferences.displayCompletedPurchases
): List<ProductItem> {
    return getOtherProducts(displayCompleted).map { toProductItem(it) }
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

private fun Products.toProductItem(product: Product): ProductItem {
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted()

    val brand = if (product.brand.isEmpty()) "" else " ${product.brand}"
    val manufacturer = if (product.manufacturer.isEmpty()) "" else " • ${product.manufacturer}"
    val nameText: UiText = UiText.FromString("${product.name}$brand$manufacturer")

    var otherName = product.size
    otherName += if (product.color.isEmpty()) {
        ""
    } else {
        if (product.size.isEmpty()) product.color else " • ${product.color}"
    }

    var body = otherName
    val otherDivider = if (otherName.isEmpty()) "" else " • "
    body += if (displayPrice) {
        if (displayQuantity) {
            "$otherDivider${product.quantity} • ${product.formatTotal()}"
        } else {
            "$otherDivider${product.formatTotal()}"
        }
    } else {
        if (displayQuantity) "$otherDivider${product.quantity}" else ""
    }

    if (product.note.isNotEmpty()) {
        body += if (body.isEmpty()) product.note else "\n${product.note}"
    }

    val bodyText: UiText = if (body.isEmpty()) UiText.Nothing else UiText.FromString(body)

    return ProductItem(
        uid = product.productUid,
        nameText = nameText,
        bodyText = bodyText,
        completed = product.completed
    )
}

private fun Products.toProductWidgetItem(product: Product): ProductWidgetItem {
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.formatTotal().isNotEmpty() && preferences.displayMoney && !totalFormatted()

    val body = if (displayPrice) {
        if (displayQuantity) {
            "${product.name} • ${product.quantity} • ${product.formatTotal()}"
        } else {
            "${product.name} • ${product.formatTotal()}"
        }
    } else {
        if (displayQuantity) "${product.name} • ${product.quantity}" else product.name
    }

    return ProductWidgetItem(
        uid = product.productUid,
        body = body,
        completed = product.completed
    )
}