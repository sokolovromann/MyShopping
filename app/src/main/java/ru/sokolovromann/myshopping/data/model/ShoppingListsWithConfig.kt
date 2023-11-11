package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.utils.sortedShoppingLists
import ru.sokolovromann.myshopping.data.utils.toShoppingsList

data class ShoppingListsWithConfig(
    private val shoppingLists: List<ShoppingList> = listOf(),
    private val appConfig: AppConfig = AppConfig()
) {

    fun getSortedShoppingLists(
        displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
    ): List<ShoppingList> {
        return getPinnedOtherSortedShoppingLists(displayCompleted).toShoppingsList()
    }

    fun getPinnedOtherSortedShoppingLists(
        displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
    ): Pair<List<ShoppingList>, List<ShoppingList>> {
        return shoppingLists
            .sortedShoppingLists(displayCompleted = displayCompleted)
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
        var total = 0f
        shoppingLists.forEach {
            total += it.shopping.total.value
        }

        return Money(
            value = total,
            currency = appConfig.userPreferences.currency,
            asPercent = false,
            decimalFormat = appConfig.userPreferences.moneyDecimalFormat
        )
    }

    fun calculateTotalByUids(uids: List<String>): Money {
        var total = 0f
        shoppingLists.forEach {
            if (uids.contains(it.shopping.uid)) {
                total += it.shopping.total.value
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
        val hideCompleted = getUserPreferences().displayCompleted == DisplayCompleted.HIDE
        val hasCompletedShoppingLists = shoppingLists.find { it.isCompleted() } != null
        return hideCompleted && hasCompletedShoppingLists
    }

    fun isEmpty(): Boolean {
        return shoppingLists.isEmpty()
    }
}