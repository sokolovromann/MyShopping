package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun DisplayProducts.toPurchasesSettingsText(): UiText = when (this) {
    DisplayProducts.COLUMNS -> UiText.FromResources(R.string.settings_action_displayShoppingsProductsColumns)
    DisplayProducts.ROW -> UiText.FromResources(R.string.settings_action_displayShoppingsProductsRow)
    DisplayProducts.HIDE -> UiText.FromResources(R.string.settings_action_hideShoppingsProducts)
}