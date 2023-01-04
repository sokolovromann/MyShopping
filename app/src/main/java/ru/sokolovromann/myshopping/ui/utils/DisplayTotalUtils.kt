package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun DisplayTotal.toShoppingListsText(): UiText = when (this) {
    DisplayTotal.ALL -> UiText.FromResources(R.string.shoppingLists_action_displayAllTotal)
    DisplayTotal.COMPLETED -> UiText.FromResources(R.string.shoppingLists_action_displayCompletedTotal)
    DisplayTotal.ACTIVE -> UiText.FromResources(R.string.shoppingLists_action_displayActiveTotal)
}

fun DisplayTotal.toProductsText(): UiText = when (this) {
    DisplayTotal.ALL -> UiText.FromResources(R.string.products_action_displayAllTotal)
    DisplayTotal.COMPLETED -> UiText.FromResources(R.string.products_action_displayCompletedTotal)
    DisplayTotal.ACTIVE -> UiText.FromResources(R.string.products_action_displayActiveTotal)
}