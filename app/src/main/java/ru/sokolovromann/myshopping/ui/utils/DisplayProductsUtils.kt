package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun DisplayProducts.toPurchasesSettingsText(): UiText = when (this) {
    DisplayProducts.VERTICAL -> UiText.FromResources(R.string.settings_action_displayShoppingsProductsColumns)
    DisplayProducts.HORIZONTAL -> UiText.FromResources(R.string.settings_action_displayShoppingsProductsRow)
    DisplayProducts.HIDE -> UiText.FromResources(R.string.settings_action_hideShoppingsProducts)
    DisplayProducts.HIDE_IF_HAS_TITLE -> UiText.FromResources(R.string.settings_action_hideShoppingsProductsIfHasTitle)
}