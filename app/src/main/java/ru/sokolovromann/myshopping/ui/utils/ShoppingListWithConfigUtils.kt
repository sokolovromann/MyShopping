package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.compose.state.toUiTextOrNothing

fun ShoppingListWithConfig.getActivePinnedProductWidgetItems(): List<ProductWidgetItem> {
    return getPinnedOtherSortedProducts().first.map { toProductWidgetItem(it) }
}

fun ShoppingListWithConfig.getOtherProductWidgetItems(): List<ProductWidgetItem> {
    return getPinnedOtherSortedProducts().second.map { toProductWidgetItem(it) }
}

fun ShoppingListWithConfig.getActivePinnedProductItems(
    displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
): List<ProductItem> {
    return getPinnedOtherSortedProducts(displayCompleted).first.map { toProductItem(it) }
}

fun ShoppingListWithConfig.getOtherProductItems(
    displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
): List<ProductItem> {
    return getPinnedOtherSortedProducts(displayCompleted).second.map { toProductItem(it) }
}

fun ShoppingListWithConfig.getTotal(): UiText {
    val total = getShopping().total.getDisplayValue()
    return if (getShopping().totalFormatted) {
        UiText.FromResourcesWithArgs(R.string.products_text_totalFormatted, total)
    } else {
        val id = when (getUserPreferences().displayTotal) {
            DisplayTotal.ALL -> R.string.products_text_allTotal
            DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
            DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
        }
        UiText.FromResourcesWithArgs(id, total)
    }
}

fun ShoppingListWithConfig.getSelectedTotal(productUids: List<String>): UiText {
    val total = calculateTotalByProductUids(productUids)
    return UiText.FromResourcesWithArgs(R.string.products_text_selectedTotal, total.getDisplayValue())
}

fun ShoppingListWithConfig.getShareText(): Result<String> {
    return if (isProductsEmpty()) {
        val exception = IllegalArgumentException("You have no products")
        Result.failure(exception)
    } else {
        val displayMoney = getUserPreferences().displayMoney

        val success = StringBuilder()

        val shoppingName = getShopping().name
        val displayName = shoppingName.isNotEmpty()
        if (displayName) {
            success.append(shoppingName)
            success.append(":\n")
        }

        val totalFormatted = getShopping().totalFormatted
        getSortedProducts().forEach {
            if (!it.completed) {
                success.append("- ")

                val title = getProductName(it, getUserPreferences())
                success.append(title)

                val body = getProductBody(it, totalFormatted, getUserPreferences())
                if (body.isNotEmpty()) {
                    success.append(getUserPreferences().purchasesSeparator)
                    success.append(body)
                }

                success.append("\n")
            }
        }

        val total = if (getShopping().totalFormatted) {
            getShopping().total
        } else {
            calculateTotalByDisplayTotal(DisplayTotal.ACTIVE)
        }
        if (displayMoney && total.isNotEmpty()) {
            success.append("\n= ")
            success.append(total.getDisplayValue())
        } else {
            if (success.isNotEmpty()) {
                success.dropLast(1)
            }
        }

        Result.success(success.toString())
    }
}

private fun ShoppingListWithConfig.toProductWidgetItem(product: Product): ProductWidgetItem {
    val displayMoney = getUserPreferences().displayMoney
    val displayQuantity = product.quantity.isNotEmpty()
    val displayPrice = product.total.isNotEmpty() && displayMoney && !product.totalFormatted

    val body = if (displayPrice) {
        if (displayQuantity) {
            "${product.name} • ${product.quantity} • ${product.total}"
        } else {
            "${product.name} • ${product.total}"
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

private fun ShoppingListWithConfig.toProductItem(product: Product): ProductItem {
    val totalFormatted = getShopping().totalFormatted
    return ProductItem(
        uid = product.productUid,
        nameText = getProductName(product, getUserPreferences()).toUiTextOrNothing(),
        bodyText = getProductBody(product, totalFormatted, getUserPreferences()).toUiTextOrNothing(),
        completed = product.completed
    )
}

private fun getProductName(product: Product, userPreferences: UserPreferences): String {
    val displayOtherFields = userPreferences.displayOtherFields
    val separator = userPreferences.purchasesSeparator

    val builder = StringBuilder(product.name)

    val displayBrand = displayOtherFields && product.brand.isNotEmpty()
    if (displayBrand) {
        builder.append(" ${product.brand}")
    }

    val displayManufacturer = displayOtherFields && product.manufacturer.isNotEmpty()
    if (displayManufacturer) {
        builder.append("$separator${product.manufacturer}")
    }

    return builder.toString()
}

private fun getProductBody(
    product: Product,
    shoppingTotalFormatted: Boolean,
    userPreferences: UserPreferences
): String {
    val displayOtherFields = userPreferences.displayOtherFields
    val displayMoney = userPreferences.displayMoney
    val separator = userPreferences.purchasesSeparator

    val builder = StringBuilder()

    val displaySize = displayOtherFields && product.size.isNotEmpty()
    if (displaySize) {
        builder.append(product.size)
    }

    val displayColor = displayOtherFields && product.color.isNotEmpty()
    if (displayColor) {
        if (builder.isNotEmpty()) {
            builder.append(separator)
        }
        builder.append(product.color)
    }

    if (builder.isNotEmpty()) {
        builder.append(separator)
    }

    val displayQuantity = product.quantity.isNotEmpty()
    val displayTotal = displayMoney && !shoppingTotalFormatted && product.total.isNotEmpty()

    if (displayTotal) {
        if (displayQuantity) {
            builder.append(product.quantity)
            builder.append(separator)
        }
        builder.append(product.total)
    } else {
        if (displayQuantity) {
            builder.append(product.quantity)
        }
    }

    val displayNote = product.note.isNotEmpty()
    if (displayNote) {
        if (builder.isNotEmpty()) {
            builder.append("\n")
        }
        builder.append(product.note)
    }

    return builder.toString()
}