package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.compose.state.ProductItem
import ru.sokolovromann.myshopping.ui.compose.state.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.compose.state.toUiTextOrNothing

fun ShoppingListWithConfig.getActivePinnedProductWidgetItems(): List<ProductWidgetItem> {
    return getPinnedOtherProducts().first.map { toProductWidgetItem(it) }
}

fun ShoppingListWithConfig.getOtherProductWidgetItems(): List<ProductWidgetItem> {
    return getPinnedOtherProducts().second.map { toProductWidgetItem(it) }
}

fun ShoppingListWithConfig.getActivePinnedProductItems(): List<ProductItem> {
    return getPinnedOtherProducts().first.map { toProductItem(it) }
}

fun ShoppingListWithConfig.getOtherProductItems(): List<ProductItem> {
    return getPinnedOtherProducts().second.map { toProductItem(it) }
}

fun ShoppingListWithConfig.getTotal(): UiText {
    val total = shoppingList.shopping.total.getDisplayValue()
    return if (shoppingList.shopping.totalFormatted) {
        UiText.FromResourcesWithArgs(R.string.products_text_totalFormatted, total)
    } else {
        val id = when (appConfig.userPreferences.displayTotal) {
            DisplayTotal.ALL -> R.string.products_text_allTotal
            DisplayTotal.COMPLETED -> R.string.products_text_completedTotal
            DisplayTotal.ACTIVE -> R.string.products_text_activeTotal
        }
        UiText.FromResourcesWithArgs(id, total)
    }
}

fun ShoppingListWithConfig.getSelectedTotal(productUids: List<String>): UiText {
    val total = shoppingList.calculateTotalByProductUids(productUids)
    return UiText.FromResourcesWithArgs(R.string.products_text_selectedTotal, total.getDisplayValue())
}

fun ShoppingListWithConfig.getShareText(): Result<String> {
    return if (shoppingList.products.isEmpty()) {
        val exception = IllegalArgumentException("You have no products")
        Result.failure(exception)
    } else {
        val displayMoney = appConfig.userPreferences.displayMoney

        val success = StringBuilder()

        val shoppingName = shoppingList.shopping.name
        val displayName = shoppingName.isNotEmpty()
        if (displayName) {
            success.append(shoppingName)
            success.append(":\n")
        }

        shoppingList.products.forEach {
            if (!it.completed) {
                success.append("- ")

                val title = getProductName(it, appConfig.userPreferences)
                success.append(title)

                val body = getProductBody(it, appConfig.userPreferences)
                if (body.isNotEmpty()) {
                    success.append(appConfig.userPreferences.purchasesSeparator)
                    success.append(body)
                }

                success.append("\n")
            }
        }

        val total = if (shoppingList.shopping.totalFormatted) {
            shoppingList.shopping.total
        } else {
            shoppingList.calculateTotalByDisplayTotal(DisplayTotal.COMPLETED)
        }
        if (displayMoney && total.isNotEmpty()) {
            success.append("\n=")
            success.append(total.getDisplayValue())
        } else {
            if (success.isNotEmpty()) {
                success.dropLast(1)
            }
        }

        Result.success(success.toString())
    }
}

private fun ShoppingListWithConfig.getPinnedOtherProducts(): Pair<List<Product>, List<Product>> {
    return shoppingList.products.partition { !it.completed && it.pinned }
}

private fun ShoppingListWithConfig.toProductWidgetItem(product: Product): ProductWidgetItem {
    val displayMoney = appConfig.userPreferences.displayMoney
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
    return ProductItem(
        uid = product.productUid,
        nameText = getProductName(product, appConfig.userPreferences).toUiTextOrNothing(),
        bodyText = getProductBody(product, appConfig.userPreferences).toUiTextOrNothing(),
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

private fun getProductBody(product: Product, userPreferences: UserPreferences): String {
    val displayOtherFields = userPreferences.displayOtherFields
    val displayMoney = userPreferences.displayMoney
    val separator = userPreferences.purchasesSeparator

    val builder = StringBuilder(product.size)

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
    val displayPrice = displayMoney && !product.totalFormatted && product.total.isNotEmpty()


    if (displayPrice) {
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