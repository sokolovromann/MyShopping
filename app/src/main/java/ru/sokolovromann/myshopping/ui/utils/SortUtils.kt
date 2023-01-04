package ru.sokolovromann.myshopping.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.Sort
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.compose.state.UiIcon
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun Sort.getShoppingListsText(): UiText = when (sortBy) {
    SortBy.CREATED -> UiText.FromResources(R.string.shoppingLists_action_sortByCreated)
    SortBy.LAST_MODIFIED -> UiText.FromResources(R.string.shoppingLists_action_sortByLastModified)
    SortBy.NAME -> UiText.FromResources(R.string.shoppingLists_action_sortByName)
    SortBy.TOTAL -> UiText.FromResources(R.string.shoppingLists_action_sortByTotal)
}

fun Sort.getProductsText(): UiText = when (sortBy) {
    SortBy.CREATED -> UiText.FromResources(R.string.products_action_sortByCreated)
    SortBy.LAST_MODIFIED -> UiText.FromResources(R.string.products_action_sortByLastModified)
    SortBy.NAME -> UiText.FromResources(R.string.products_action_sortByName)
    SortBy.TOTAL -> UiText.FromResources(R.string.products_action_sortByTotal)
}

fun Sort.getAutocompletesText(): UiText = when (sortBy) {
    SortBy.CREATED -> UiText.FromResources(R.string.autocompletes_action_sortByCreated)
    SortBy.NAME -> UiText.FromResources(R.string.autocompletes_action_sortByName)
    else -> UiText.Nothing
}

fun Sort.getAscendingIcon(): UiIcon {
    return if (ascending) {
        UiIcon.FromVector(Icons.Default.KeyboardArrowUp)
    } else {
        UiIcon.FromVector(Icons.Default.KeyboardArrowDown)
    }
}