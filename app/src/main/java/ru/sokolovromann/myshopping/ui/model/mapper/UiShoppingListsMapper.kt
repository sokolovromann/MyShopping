package ru.sokolovromann.myshopping.ui.model.mapper

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceConfig
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.Money
import ru.sokolovromann.myshopping.data.model.Product
import ru.sokolovromann.myshopping.data.model.ShoppingList
import ru.sokolovromann.myshopping.data.model.ShoppingListsWithConfig
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.UserPreferences
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.ShoppingListItem
import ru.sokolovromann.myshopping.ui.model.UiString
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

    fun toSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return shoppingListsWithConfig.getSortedShoppingLists(displayCompleted).map {
            toShoppingListItems(
                shoppingList = it,
                deviceConfig = shoppingListsWithConfig.getDeviceConfig(),
                userPreferences = shoppingListsWithConfig.getUserPreferences()
            )
        }
    }

    fun toPinnedSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return shoppingListsWithConfig.getPinnedOtherSortedShoppingLists(displayCompleted).first.map {
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
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            toPinnedSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        } else {
            listOf()
        }
    }

    fun toOtherSortedShoppingListItems(
        shoppingListsWithConfig: ShoppingListsWithConfig,
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return shoppingListsWithConfig.getPinnedOtherSortedShoppingLists(displayCompleted).second.map {
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
        displayCompleted: DisplayCompleted = shoppingListsWithConfig.getUserPreferences().displayCompleted
    ): List<ShoppingListItem> {
        return if (location == ShoppingLocation.PURCHASES) {
            toOtherSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        } else {
            toSortedShoppingListItems(shoppingListsWithConfig, displayCompleted)
        }
    }

    fun toOldShoppingListItems(items: List<ShoppingListItem>): List<ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem> {
        return items.map {
            ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem(
                uid = it.uid,
                nameText = it.name.toUiText(),
                productsList = it.products.map { product ->
                    Pair(product.first, product.second.toUiText())
                },
                totalText = it.total.toUiText(),
                reminderText = it.reminder.toUiText(),
                completed = it.completed
            )
        }
    }

    private fun toShoppingListItems(
        shoppingList: ShoppingList,
        deviceConfig: DeviceConfig,
        userPreferences: UserPreferences
    ): ShoppingListItem {
        val displayShoppingsProducts = userPreferences.displayShoppingsProducts
        val displayTotal = userPreferences.displayTotal == DisplayTotal.ALL

        val name = shoppingList.shopping.name
        val displayUnnamed = displayShoppingsProducts == DisplayProducts.HIDE && name.isEmpty()
        val nameText: UiString = if (displayUnnamed) {
            UiString.FromResources(R.string.shoppingLists_text_nameNotFound)
        } else {
            UiString.FromString(name)
        }

        val maxShoppingProducts = 10
        val totalFormatted = shoppingList.shopping.totalFormatted && displayTotal
        val productsList = if (shoppingList.products.isEmpty()) {
            val pair = Pair(null, UiString.FromResources(R.string.purchases_text_productsNotFound))
            listOf(pair)
        } else {
            val products: MutableList<Pair<Boolean?, UiString>> = shoppingList.products
                .sortedByDescending { it.pinned }
                .filterIndexed { index, _ -> index < maxShoppingProducts }
                .map {
                    productsToPair(
                        product = it,
                        totalFormatted = totalFormatted,
                        deviceConfig = deviceConfig,
                        userPreferences = userPreferences
                    )
                }.toMutableList()

            if (shoppingList.products.size > maxShoppingProducts) {
                val moreProducts = Pair(null, UiString.FromResources(R.string.purchases_text_moreProducts))
                products.add(moreProducts)
            }

            products.toList()
        }

        val totalText: UiString = toTotalValue(
            total = shoppingList.shopping.total,
            totalFormatted = shoppingList.shopping.totalFormatted,
            userPreferences = userPreferences
        )?.text ?: UiString.FromString("")

        val reminderText: UiString = if (shoppingList.shopping.reminder == null) {
            UiString.FromString("")
        } else {
            shoppingList.shopping.reminder.toCalendar().getDisplayDateAndTime()
        }

        return ShoppingListItem(
            uid = shoppingList.shopping.uid,
            name = nameText,
            products = productsList,
            total = totalText,
            reminder = reminderText,
            completed = shoppingList.isCompleted()
        )
    }

    private fun productsToPair(
        product: Product,
        totalFormatted: Boolean,
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
            second = builder.toUiString()
        )
    }
}