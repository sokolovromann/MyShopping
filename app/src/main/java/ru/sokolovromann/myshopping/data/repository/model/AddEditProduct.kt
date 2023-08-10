package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProduct(
    val product: Product? = null,
    val productsLastPosition: Int? = null,
    val preferences: AppPreferences = AppPreferences(),
    val appConfig: AppConfig = AppConfig()
) {

    fun formatName(): String {
        return product?.name ?: ""
    }
}