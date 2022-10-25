package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProduct(
    val product: Product = Product(),
    val preferences: ProductPreferences = ProductPreferences()
) {

    fun formatName(): String {
        return product.name.formatFirst(preferences.firstLetterUppercase)
    }
}