package ru.sokolovromann.myshopping.data.model

import java.math.BigDecimal

class ShoppingListWithConfig(
    private val shoppingList: ShoppingList = ShoppingList(),
    private val appConfig: AppConfig = AppConfig()
) {

    fun getShopping(): Shopping {
        return shoppingList.shopping
    }

    fun getSortedProducts(
        displayCompleted: DisplayCompleted = getUserPreferences().appDisplayCompleted
    ): List<Product> {
        return shoppingList.getSortedProducts(displayCompleted)
    }

    fun getPinnedOtherSortedProducts(
        displayCompleted: DisplayCompleted = getUserPreferences().appDisplayCompleted
    ): Pair<List<Product>, List<Product>> {
        return shoppingList.getPinnedOtherSortedProducts(displayCompleted)
    }

    fun getProductUids(): List<String> {
        return shoppingList.getProductUids()
    }

    fun getProductsByUids(productUids: List<String>): List<Product> {
        return shoppingList.getProductsByUids(productUids)
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

    fun getTotalWithoutDiscount(): Money {
        return shoppingList.getTotalWithoutDiscount()
    }

    fun calculateTotalByProductUids(productUids: List<String>): Money {
        return shoppingList.calculateTotalByProductUids(productUids)
    }

    fun calculateTotalByDisplayTotal(displayTotal: DisplayTotal): Money {
        return shoppingList.calculateTotalByDisplayTotal(displayTotal)
    }

    fun calculateCostByProductUids(productUids: List<String>): Money {
        return shoppingList.calculateCostByProductUids(productUids)
    }

    fun calculateCostByDisplayTotal(
        displayTotal: DisplayTotal = appConfig.userPreferences.displayTotal
    ): Money {
        return shoppingList.calculateCostByDisplayTotal(displayTotal)
    }

    fun calculateDiscountsByProductUids(productUids: List<String>): Money {
        return shoppingList.calculateDiscountsByProductUids(productUids)
    }

    fun calculateDiscountsByDisplayTotal(
        displayTotal: DisplayTotal = appConfig.userPreferences.displayTotal
    ): Money {
        return shoppingList.calculateDiscountsByDisplayTotal(displayTotal)
    }

    fun calculateTaxRatesByProductUids(productUids: List<String>): Money {
        return shoppingList.calculateTaxRatesByProductUids(productUids, getUserPreferences().taxRate)
    }

    fun calculateTaxRatesByDisplayTotal(
        displayTotal: DisplayTotal = appConfig.userPreferences.displayTotal
    ): Money {
        return shoppingList.calculateTaxRatesByDisplayTotal(displayTotal, getUserPreferences().taxRate)
    }

    fun calculateChange(userMoneyValue: BigDecimal): Money {
        val value = userMoneyValue.minus(shoppingList.shopping.total.value)
        return Money(
            value = value,
            currency = appConfig.userPreferences.currency,
            decimalFormat = appConfig.userPreferences.moneyDecimalFormat
        )
    }

    fun hasHiddenProducts(): Boolean {
        val hideCompleted = getUserPreferences().appDisplayCompleted == DisplayCompleted.HIDE
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