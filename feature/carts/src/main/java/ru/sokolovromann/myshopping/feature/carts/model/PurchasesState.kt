package ru.sokolovromann.myshopping.feature.carts.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.PurchasesStatus
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Immutable
data class PurchasesState(
    val isLoading: Boolean = true,
    val status: PurchasesStatus = PurchasesStatus.NotFound,
    val pinnedItems: List<CartItem> = emptyList(),
    val otherItems: List<CartItem> = emptyList(),
    val itemsCells: Int = 1,
    val itemsSelected: Set<UID> = emptySet(),
    val total: UiText? = null
)