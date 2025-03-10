package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceConfig
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.data.utils.asSearchQuery
import ru.sokolovromann.myshopping.ui.model.ProductItem
import ru.sokolovromann.myshopping.ui.model.ProductWidgetItem
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.ShoppingListItem
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.utils.getDisplayDate
import ru.sokolovromann.myshopping.ui.utils.getDisplayDateAndTime
import ru.sokolovromann.myshopping.ui.utils.toUiString

object UiShoppingListsMapper {

    fun toTotalValue(
        total: Money,
        totalFormatted: Boolean,
        userPreferences: UserPreferences
    ): SelectedValue<DisplayTotal>? {
        if (!userPreferences.displayMoney) {
            return null
        }

        val text: UiString = if (totalFormatted) {
            UiString.FromResourcesWithArgs(R.string.shoppingLists_text_totalFormatted, total.getDisplayValue())
        } else {
            val id = when (userPreferences.displayTotal) {
                DisplayTotal.ALL -> R.string.shoppingLists_text_allTotal
                DisplayTotal.COMPLETED -> R.string.shoppingLists_text_completedTotal
                DisplayTotal.ACTIVE -> R.string.shoppingLists_text_activeTotal
            }
            UiString.FromResourcesWithArgs(id, total.getDisplayValue())
        }

        return SelectedValue(
            selected = userPreferences.displayTotal,
            text = text
        )
    }

    fun toMultiColumnsValue(multiColumns: Boolean): SelectedValue<Boolean> {
        return SelectedValue(
            selected = multiColumns,
            text = if (multiColumns) {
                UiString.FromResources(R.string.shoppingLists_action_disableShoppingsMultiColumns)
            } else {
                UiString.FromResources(R.string.shoppingLists_action_enableShoppingsMultiColumns)
            }
        )
    }

    fun toShoppingListPinned(pinned: Boolean): SelectedValue<Boolean> {
        return SelectedValue(
            selected = pinned,
            text = if (pinned) {
                UiString.FromResources(R.string.shoppingLists_action_selectUnpinShoppingList)
            } else {
                UiString.FromResources(R.string.shoppingLists_action_selectPinShoppingList)
            }
        )
    }

    fun toLocationValue(location: ShoppingLocation): SelectedValue<ShoppingLocation> {
        return SelectedValue(
            selected = location,
            text = when (location) {
                ShoppingLocation.PURCHASES -> UiString.FromResources(R.string.shoppingLists_action_selectPurchasesLocation)
                ShoppingLocation.ARCHIVE -> UiString.FromResources(R.string.shoppingLists_action_selectArchiveLocation)
                ShoppingLocation.TRASH -> UiString.FromResources(R.string.shoppingLists_action_selectTrashLocation)
            }
        )
    }

    fun toShoppingListString(
        shoppingListWithConfig: ShoppingListWithConfig,
        displayTotal: DisplayTotal
    ): String {
        val shopping = shoppingListWithConfig.getShopping()
        val userPreferences = shoppingListWithConfig.getUserPreferences()
        val displayMoney = userPreferences.displayMoney

        val success = StringBuilder()

        val shoppingName = shopping.name
        val displayName = shoppingName.isNotEmpty()
        if (displayName) {
            success.append(shoppingName)
            success.append(":\n")
        }

        shoppingListWithConfig.getSortedProducts().forEach {
            when (displayTotal) {
                DisplayTotal.ALL -> {
                    if (it.completed) {
                        success.append("+ ")
                    } else {
                        success.append("- ")
                    }

                    val title = getProductName(
                        product = it,
                        userPreferences = userPreferences
                    )
                    success.append(title)

                    val body = getProductBody(
                        product = it,
                        userPreferences = userPreferences
                    )
                    if (body.isNotEmpty()) {
                        success.append(userPreferences.purchasesSeparator)
                        success.append(body)
                    }

                    success.append("\n")
                }

                DisplayTotal.COMPLETED -> {
                    if (it.completed) {
                        success.append("+ ")

                        val title = getProductName(
                            product = it,
                            userPreferences = userPreferences
                        )
                        success.append(title)

                        val body = getProductBody(
                            product = it,
                            userPreferences = userPreferences
                        )
                        if (body.isNotEmpty()) {
                            success.append(userPreferences.purchasesSeparator)
                            success.append(body)
                        }

                        success.append("\n")
                    }
                }

                DisplayTotal.ACTIVE -> {
                    if (!it.completed) {
                        success.append("- ")

                        val title = getProductName(
                            product = it,
                            userPreferences = userPreferences
                        )
                        success.append(title)

                        val body = getProductBody(
                            product = it,
                            userPreferences = userPreferences
                        )
                        if (body.isNotEmpty()) {
                            success.append(userPreferences.purchasesSeparator)
                            success.append(body)
                        }

                        success.append("\n")
                    }
                }
            }
        }

        val total = if (shopping.totalFormatted) {
            shopping.total
        } else {
            shoppingListWithConfig.calculateTotalByDisplayTotal(displayTotal)
        }
        if (displayMoney && total.isNotEmpty()) {
            success.append("\n= ")
            success.append(total.getDisplayValue())
        } else {
            if (success.isNotEmpty()) {
                success.dropLast(1)
            }
        }

        return success.toString()
    }

    fun toSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        search: String? = null,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = shoppingListsWithConfig.getUserPreferences().displayEmptyShoppings
    ): List<ShoppingListItem> {
        val shoppingLists = shoppingListsWithConfig.getSortedShoppingLists(displayCompleted, displayEmptyShoppings)
        return (if (search == null) {
            shoppingLists
        } else {
            shoppingLists.filter {
                it.shopping.name.asSearchQuery().contains(search.asSearchQuery()) ||
                        it.products.any { product ->
                            product.name.asSearchQuery().contains(search.asSearchQuery())
                        }
            }
        }).map {
            toShoppingListItems(
                shoppingList = it,
                deviceConfig = shoppingListsWithConfig.getDeviceConfig(),
                userPreferences = shoppingListsWithConfig.getUserPreferences()
            )
        }
    }

    fun toPinnedSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        search: String? = null,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = shoppingListsWithConfig.getUserPreferences().displayEmptyShoppings
    ): List<ShoppingListItem> {
        val shoppingLists = shoppingListsWithConfig.getPinnedOtherSortedShoppingLists(displayCompleted, displayEmptyShoppings).first
        return (if (search == null) {
            shoppingLists
        } else {
            shoppingLists.filter {
                it.shopping.name.asSearchQuery().contains(search.asSearchQuery()) ||
                        it.products.any { product ->
                            product.name.asSearchQuery().contains(search.asSearchQuery())
                        }
            }
        }).map {
            toShoppingListItems(
                shoppingList = it,
                deviceConfig = shoppingListsWithConfig.getDeviceConfig(),
                userPreferences = shoppingListsWithConfig.getUserPreferences()
            )
        }
    }

    fun toPinnedSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        location: ShoppingLocation,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = shoppingListsWithConfig.getUserPreferences().displayEmptyShoppings
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            toPinnedSortedShoppingListItems(shoppingListsWithConfig, null, displayCompleted, displayEmptyShoppings)
        } else {
            listOf()
        }
    }

    fun toOtherSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        search: String? = null,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = shoppingListsWithConfig.getUserPreferences().displayEmptyShoppings
    ): List<ShoppingListItem> {
        val shoppingLists = shoppingListsWithConfig.getPinnedOtherSortedShoppingLists(displayCompleted, displayEmptyShoppings).second
        return (if (search == null) {
            shoppingLists
        } else {
            shoppingLists.filter {
                it.shopping.name.asSearchQuery().contains(search.asSearchQuery()) ||
                        it.products.any { product ->
                            product.name.asSearchQuery().contains(search.asSearchQuery())
                        }
            }
        }).map {
            toShoppingListItems(
                shoppingList = it,
                deviceConfig = shoppingListsWithConfig.getDeviceConfig(),
                userPreferences = shoppingListsWithConfig.getUserPreferences()
            )
        }
    }

    fun toOtherSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        location: ShoppingLocation,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = shoppingListsWithConfig.getUserPreferences().displayEmptyShoppings
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            toOtherSortedShoppingListItems(shoppingListsWithConfig, null, displayCompleted, displayEmptyShoppings)
        } else {
            toSortedShoppingListItems(shoppingListsWithConfig, null, displayCompleted, displayEmptyShoppings)
        }
    }

    fun toSortedProductItems(
        shoppingListWithConfig: ShoppingListWithConfig,
        displayCompleted: DisplayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted,
    ): List<ProductItem> {
        return shoppingListWithConfig.getSortedProducts(displayCompleted).map {
            toProductItem(
                product = it,
                userPreferences = shoppingListWithConfig.getUserPreferences()
            )
        }
    }

    fun toPinnedSortedProductItems(
        shoppingListWithConfig: ShoppingListWithConfig,
        search: String? = null,
        displayCompleted: DisplayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted
    ): List<ProductItem> {
        val products = shoppingListWithConfig.getPinnedOtherSortedProducts(displayCompleted).first
        return (if (search == null) {
            products
        } else {
            products.filter { it.name.asSearchQuery().contains(search.asSearchQuery()) }
        }).map {
            toProductItem(
                product = it,
                userPreferences = shoppingListWithConfig.getUserPreferences()
            )
        }
    }

    fun toOtherSortedProductItems(
        shoppingListWithConfig: ShoppingListWithConfig,
        search: String? = null,
        displayCompleted: DisplayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted
    ): List<ProductItem> {
        val products = shoppingListWithConfig.getPinnedOtherSortedProducts(displayCompleted).second
        return (if (search == null) {
            products
        } else {
            products.filter { it.name.asSearchQuery().contains(search.asSearchQuery()) }
        }).map {
            toProductItem(
                product = it,
                userPreferences = shoppingListWithConfig.getUserPreferences()
            )
        }
    }

    fun toPinnedSortedProductWidgetItems(
        shoppingListWithConfig: ShoppingListWithConfig,
    ): List<ProductWidgetItem> {
        return shoppingListWithConfig.getPinnedOtherSortedProducts().first.map {
            toProductWidgetItem(
                product = it,
                userPreferences = shoppingListWithConfig.getUserPreferences()
            )
        }
    }

    fun toOtherSortedProductWidgetItems(
        shoppingListWithConfig: ShoppingListWithConfig,
        displayCompleted: DisplayCompleted = shoppingListWithConfig.getUserPreferences().appDisplayCompleted
    ): List<ProductWidgetItem> {
        return shoppingListWithConfig.getPinnedOtherSortedProducts(displayCompleted).second.map {
            toProductWidgetItem(
                product = it,
                userPreferences = shoppingListWithConfig.getUserPreferences()
            )
        }
    }

    private fun toShoppingListItems(
        shoppingList: ShoppingList,
        deviceConfig: DeviceConfig,
        userPreferences: UserPreferences
    ): ShoppingListItem {
        val displayShoppingsProducts = userPreferences.displayShoppingsProducts

        val name = shoppingList.shopping.name
        val displayUnnamed = displayShoppingsProducts == DisplayProducts.HIDE && name.isEmpty()
        val nameText: UiString = if (displayUnnamed) {
            UiString.FromResources(R.string.shoppingLists_text_nameNotFound)
        } else {
            UiString.FromString(name)
        }

        val maxShoppingProducts = 10
        val productsList = if (shoppingList.isProductsEmpty()) {
            val pair = Pair(null, UiString.FromResources(R.string.shoppingLists_text_productsNotFound))
            listOf(pair)
        } else {
            val products: MutableList<Pair<Boolean?, UiString>> = shoppingList.products
                .sortedByDescending { it.pinned }
                .filterIndexed { index, _ -> index < maxShoppingProducts }
                .map {
                    productsToPair(
                        product = it,
                        deviceConfig = deviceConfig,
                        userPreferences = userPreferences
                    )
                }.toMutableList()

            if (shoppingList.products.size > maxShoppingProducts) {
                val moreProducts = Pair(null, UiString.FromResources(R.string.shoppingLists_text_moreProducts))
                products.add(moreProducts)
            }

            products.toList()
        }

        val totalValue = toTotalValue(
            total = shoppingList.shopping.total,
            totalFormatted = shoppingList.shopping.totalFormatted,
            userPreferences = userPreferences
        )
        val budget = shoppingList.shopping.budget
        val budgetText: UiString = if (
            totalValue == null ||
            budget.isEmpty() ||
            userPreferences.displayTotal != shoppingList.shopping.budgetProducts ||
            shoppingList.shopping.totalFormatted
        ) {
            UiString.FromString("")
        } else {
            UiString.FromResourcesWithUiString(
                R.string.shoppingLists_text_of,
                budget.getDisplayValue().toUiString(),
                " ".toUiString(),
                true
            )
        }
        val totalText: UiString = if (totalValue == null || shoppingList.shopping.location == ShoppingLocation.TRASH) {
            UiString.FromString("")
        } else {
            UiString.FromUiStrings(arrayOf(totalValue.text, budgetText), " ".toUiString())
        }

        val reminderText: UiString = if (shoppingList.shopping.reminder == null) {
            UiString.FromString("")
        } else {
            shoppingList.shopping.reminder.toCalendar().getDisplayDateAndTime()
        }

        val lastModified: UiString = shoppingList.shopping.lastModified.toCalendar().getDisplayDate()
        val separator: UiString = UiString.FromString(": ")
        val lastModifiedText: UiString = when (shoppingList.shopping.location) {
            ShoppingLocation.PURCHASES -> UiString.FromString("")
            ShoppingLocation.ARCHIVE -> UiString.FromString("")
            ShoppingLocation.TRASH -> UiString.FromResourcesWithUiString(R.string.shoppingLists_text_deleted, lastModified, separator)
        }

        return ShoppingListItem(
            uid = shoppingList.shopping.uid,
            name = nameText,
            products = productsList,
            total = totalText,
            reminder = reminderText,
            completed = shoppingList.isCompleted(),
            lastModified = lastModifiedText
        )
    }

    private fun productsToPair(
        product: Product,
        deviceConfig: DeviceConfig,
        userPreferences: UserPreferences
    ): Pair<Boolean, UiString> {
        val multiColumns = userPreferences.shoppingsMultiColumns
        val smartphoneScreen = deviceConfig.getDeviceSize().isSmartphoneScreen()
        val separator = userPreferences.purchasesSeparator
        val displayMoney = userPreferences.displayMoney
        val displayOtherFields = userPreferences.displayOtherFields

        val builder = StringBuilder(product.name)

        val shortText = multiColumns && smartphoneScreen
        val displayVertically = userPreferences.displayShoppingsProducts == DisplayProducts.VERTICAL
        val displayProductElements = !shortText && displayVertically

        if (displayProductElements) {
            val displayBrand = displayOtherFields && product.brand.isNotEmpty()
            if (displayBrand) {
                builder.append(" ")
                builder.append(product.brand)
            }

            val displayQuantity = product.quantity.isNotEmpty()
            val displayTotal = displayMoney && product.total.isNotEmpty()

            if (displayTotal) {
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
            second = builder.toUiString()
        )
    }

    private fun toProductItem(
        product: Product,
        userPreferences: UserPreferences
    ): ProductItem {
        return ProductItem(
            uid = product.productUid,
            name = getProductName(
                product = product,
                userPreferences = userPreferences
            ).toUiString(),
            body = getProductBody(
                product = product,
                userPreferences = userPreferences
            ).toUiString(),
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

        val displayQuantity = product.quantity.isNotEmpty()
        val displayTotal = displayMoney && product.total.isNotEmpty()

        if (displayTotal) {
            if (builder.isNotEmpty()) {
                builder.append(separator)
            }

            if (userPreferences.displayLongTotal) {
                builder.append(getProductLongTotalBody(product, userPreferences.taxRate))
            } else {
                if (displayQuantity) {
                    builder.append(product.quantity)
                    builder.append(separator)
                }
                builder.append(product.total)
            }
        } else {
            if (displayQuantity) {
                if (builder.isNotEmpty()) {
                    builder.append(separator)
                }
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

    private fun getProductLongTotalBody(
        product: Product,
        userTaxRate: Money
    ): String {
        val builder = StringBuilder()
        val quantity = if (product.quantity.isEmpty()) {
            product.quantity.copy(value = 1f)
        } else {
            product.quantity
        }
        builder.append(quantity)
        builder.append(" x ")

        val price = if (product.price.isEmpty()) {
            product.total
        } else {
            product.price
        }
        builder.append(price)

        if (product.discount.isNotEmpty()) {
            builder.append(" - ")
            builder.append(product.getDiscountAsMoney())
        }

        if (product.taxRate.isNotEmpty()) {
            builder.append(" + ")
            builder.append(product.getTaxRateAsMoney(userTaxRate))
        }

        builder.append(" = ")
        builder.append(product.total)

        return builder.toString()
    }

    private fun toProductWidgetItem(
        product: Product,
        userPreferences: UserPreferences
    ): ProductWidgetItem {
        val displayMoney = userPreferences.displayMoney
        val separator = userPreferences.purchasesSeparator

        val builder = StringBuilder(product.name)

        val displayPrice = product.total.isNotEmpty() && displayMoney && !product.totalFormatted
        val displayQuantity = product.quantity.isNotEmpty()

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

        return ProductWidgetItem(
            uid = product.productUid,
            body = builder.toString(),
            completed = product.completed
        )
    }
}