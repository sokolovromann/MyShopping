package ru.sokolovromann.myshopping.data.model

class ShoppingListWithConfig(
    private val shoppingList: ShoppingList = ShoppingList(),
    private val appConfig: AppConfig = AppConfig()
) {

    fun getShopping(): Shopping {
        return shoppingList.shopping
    }

    fun getSortedProducts(
        displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
    ): List<Product> {
        return shoppingList.getSortedProducts(displayCompleted)
    }

    fun getPinnedOtherSortedProducts(
        displayCompleted: DisplayCompleted = getUserPreferences().displayCompleted
    ): Pair<List<Product>, List<Product>> {
        return shoppingList.getPinnedOtherSortedProducts(displayCompleted)
    }

    fun getProductUids(): List<String> {
        return shoppingList.getProductUids()
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

    fun calculateTotalByProductUids(productUids: List<String>): Money {
        return shoppingList.calculateTotalByProductUids(productUids)
    }

    fun calculateTotalByDisplayTotal(displayTotal: DisplayTotal): Money {
        return shoppingList.calculateTotalByDisplayTotal(displayTotal)
    }

    fun hasHiddenProducts(): Boolean {
        val hideCompleted = getUserPreferences().displayCompleted == DisplayCompleted.HIDE
        val hasCompletedProducts = shoppingList.products.find { it.completed } != null
        return hideCompleted && hasCompletedProducts
    }

    fun isCompleted(): Boolean {
        return shoppingList.isCompleted()
    }

    fun isShoppingEmpty(): Boolean {
        return shoppingList.isShoppingEmpty()
    }

    fun isProductsEmpty(): Boolean {
        return shoppingList.isProductsEmpty()
    }
}