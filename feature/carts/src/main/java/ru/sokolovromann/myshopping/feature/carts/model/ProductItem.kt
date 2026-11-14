package ru.sokolovromann.myshopping.feature.carts.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.ProductStatus

@Immutable
data class ProductItem(
    val status: ProductStatus,
    val text: String
)