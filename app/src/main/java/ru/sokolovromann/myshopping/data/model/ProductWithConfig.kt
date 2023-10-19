package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.data.repository.model.Id

data class ProductWithConfig(
    val product: Product = Product(),
    val appConfig: AppConfig = AppConfig()
) {

    fun isEmpty(): Boolean {
        return product.id == Id.NO_ID
    }
}