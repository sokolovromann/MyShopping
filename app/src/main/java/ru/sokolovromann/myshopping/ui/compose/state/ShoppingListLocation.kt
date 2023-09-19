package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.R

@Deprecated("Use ShoppingLocation")
enum class ShoppingListLocation {

    PURCHASES, ARCHIVE, TRASH;

    companion object {
        val DefaultValue: ShoppingListLocation = PURCHASES
    }

    fun getText(): UiText = when (this) {
        PURCHASES -> UiText.FromResources(R.string.shoppingLists_action_selectPurchasesLocation)
        ARCHIVE -> UiText.FromResources(R.string.shoppingLists_action_selectArchiveLocation)
        TRASH -> UiText.FromResources(R.string.shoppingLists_action_selectTrashLocation)
    }
}