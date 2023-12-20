package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.compose.state.UiText

@Deprecated("Will be deleted")
fun ShoppingLocation.getText(): UiText = when (this) {
    ShoppingLocation.PURCHASES -> UiText.FromResources(R.string.shoppingLists_action_selectPurchasesLocation)
    ShoppingLocation.ARCHIVE -> UiText.FromResources(R.string.shoppingLists_action_selectArchiveLocation)
    ShoppingLocation.TRASH -> UiText.FromResources(R.string.shoppingLists_action_selectTrashLocation)
}