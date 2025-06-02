package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.sortedShoppingLists
import ru.sokolovromann.myshopping.data.utils.toSingleList
import java.math.BigDecimal

data class ShoppingListsWithConfig(
    private val shoppingLists: List<ShoppingList> = listOf(),
    private val appConfig: AppConfig = AppConfig()
) {

    fun getSortedShoppingLists(
        displayCompleted: DisplayCompleted = getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = getUserPreferences().displayEmptyShoppings
    ): List<ShoppingList> {
        return getPinnedOtherSortedShoppingLists(displayCompleted, displayEmptyShoppings).toSingleList()
    }

    fun getPinnedOtherSortedShoppingLists(
        displayCompleted: DisplayCompleted = getUserPreferences().appDisplayCompleted,
        displayEmptyShoppings: Boolean = getUserPreferences().displayEmptyShoppings
    ): Pair<List<ShoppingList>, List<ShoppingList>> {
        val sort = if (appConfig.userPreferences.shoppingsSortFormatted) {
            appConfig.userPreferences.shoppingsSort
        } else {
            Sort()
        }
        return shoppingLists
            .sortedShoppingLists(sort, displayCompleted, displayEmptyShoppings)
            .partition { it.shopping.pinned && it.isActive() }
    }

    fun getShoppingUids(): List<String> {
        return shoppingLists.map { it.shopping.uid }
    }

    fun getDeviceConfig(): DeviceConfig {
        return appConfig.deviceConfig
    }

    fun getAppBuildConfig(): AppBuildConfig {
        return appConfig.appBuildConfig
    }

    fun getUserPreferences(): UserPreferences {
        return appConfig.userPreferences
    }

    fun getTotal(): Money {
        var total = BigDecimal.ZERO
        shoppingLists.forEach {
            if (it.shopping.totalFormatted) {
                when (getUserPreferences().displayTotal) {
                    DisplayTotal.ALL -> {
                        total = total.plus(it.shopping.total.value)
                    }
                    DisplayTotal.COMPLETED -> {
                        if (it.isCompleted()) {
                            total = total.plus(it.shopping.total.value)
                        }
                    }
                    DisplayTotal.ACTIVE -> {
                        if (it.isActive()) {
                            total = total.plus(it.shopping.total.value)
                        }
                    }
                }
            } else {
                total = total.plus(it.shopping.total.value)
            }
        }

        return Money(
            value = total,
            currency = appConfig.userPreferences.currency,
            asPercent = false,
            decimalFormat = appConfig.userPreferences.moneyDecimalFormat
        )
    }

    fun calculateTotalByUids(uids: List<String>): Money {
        var total = BigDecimal.ZERO
        shoppingLists.forEach {
            if (uids.contains(it.shopping.uid)) {
                total = total.plus(it.shopping.total.value)
            }
        }

        return Money(
            value = total,
            currency = appConfig.userPreferences.currency,
            asPercent = false,
            decimalFormat = appConfig.userPreferences.moneyDecimalFormat
        )
    }

    fun hasHiddenShoppingLists(): Boolean {
        val hideCompleted = getUserPreferences().appDisplayCompleted == DisplayCompleted.HIDE
        val hasCompleted = shoppingLists.find { it.isCompleted() } != null
        val hideAndHasCompleted = hideCompleted && hasCompleted

        val hideEmpty = !getUserPreferences().displayEmptyShoppings
        val hasEmpty = shoppingLists.find { it.isProductsEmpty() } != null
        val hideAndHasEmpty = hideEmpty && hasEmpty

        return hideAndHasCompleted || hideAndHasEmpty
    }

    fun isEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }
}