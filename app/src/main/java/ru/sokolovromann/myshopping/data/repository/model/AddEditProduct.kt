package ru.sokolovromann.myshopping.data.repository.model

data class AddEditProduct(
    val product: Product? = Product(),
    val productsLastPosition: Int?,
    val preferences: ProductPreferences = ProductPreferences()
) {

    fun formatName(): String {
        if (product == null) {
            return ""
        }
        return product.name.formatFirst(preferences.firstLetterUppercase)
    }
}