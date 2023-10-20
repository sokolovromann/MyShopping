package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
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

fun Products.getOtherProductItems(): List<ProductItem> {
    return getOtherProducts().map { toProductItem(it) }
}

fun Products.calculateTotalToText(): UiText {
    return totalToText(calculateTotal(), getDisplayTotal(), isTotalFormatted())
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
    val nameText: UiText = UiText.FromString(productToTitle(product))

    val body = productToBody(product)
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
    val displayPrice = product.formatTotal().isNotEmpty() && displayMoney() && !isTotalFormatted()

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