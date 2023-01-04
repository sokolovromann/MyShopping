package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun DisplayCompleted.toShoppingListsText(): UiText = when (this) {
    DisplayCompleted.FIRST -> UiText.FromResources(R.string.shoppingLists_action_displayCompletedFirst)
    DisplayCompleted.LAST -> UiText.FromResources(R.string.shoppingLists_action_displayCompletedLast)
    DisplayCompleted.HIDE -> UiText.FromResources(R.string.shoppingLists_action_hideCompleted)
}

fun DisplayCompleted.toProductsText(): UiText = when (this) {
    DisplayCompleted.FIRST -> UiText.FromResources(R.string.products_action_displayCompletedFirst)
    DisplayCompleted.LAST -> UiText.FromResources(R.string.products_action_displayCompletedLast)
    DisplayCompleted.HIDE -> UiText.FromResources(R.string.products_action_hideCompleted)
}