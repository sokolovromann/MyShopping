package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid

sealed class SettingsEvent {

    data class SelectSettingsItem(val uid: SettingsUid) : SettingsEvent()

    data class SelectNavigationItem(val route: UiRoute) : SettingsEvent()

    object SelectDisplayCompletedPurchases : SettingsEvent()

    data class FontSizeSelected(val fontSize: FontSize) : SettingsEvent()

    data class DisplayCompletedPurchasesSelected(val displayCompleted: DisplayCompleted) : SettingsEvent()

    data class DisplayShoppingsProductsSelected(val displayProducts: DisplayProducts) : SettingsEvent()

    object ShowBackScreen : SettingsEvent()

    object ShowNavigationDrawer : SettingsEvent()

    object HideFontSize : SettingsEvent()

    object HideNavigationDrawer : SettingsEvent()

    object HideDisplayCompletedPurchases : SettingsEvent()

    object HideDisplayShoppingsProducts : SettingsEvent()
}