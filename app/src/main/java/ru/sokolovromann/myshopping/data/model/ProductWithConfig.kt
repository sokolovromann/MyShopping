package ru.sokolovromann.myshopping.data.model

data class ProductWithConfig(
    val product: Product = Product(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return product.id == IdDefaults.NO_ID
    }
}