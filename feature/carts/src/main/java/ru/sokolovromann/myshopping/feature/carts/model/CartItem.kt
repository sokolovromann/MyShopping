package ru.sokolovromann.myshopping.feature.carts.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.PurchaseStatus
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class CartItem(
    val uid: UID,
    val status: PurchaseStatus,
    val name: String?,
    val pinnedProducts: List<ProductItem>,
    val otherProducts: List<ProductItem>,
    val reminder: String?,
    val total: UiText?
)