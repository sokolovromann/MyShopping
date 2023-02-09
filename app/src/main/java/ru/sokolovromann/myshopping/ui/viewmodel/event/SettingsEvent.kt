package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid

sealed class SettingsEvent {

    data class SelectSettingsItem(val uid: SettingsUid) : SettingsEvent()

    data class SelectNavigationItem(val route: UiRoute) : SettingsEvent()

    object SelectDisplayCompleted : SettingsEvent()

    data class FontSizeSelected(val fontSize: FontSize) : SettingsEvent()

    data class DisplayAutocompleteSelected(val displayAutocomplete: DisplayAutocomplete) : SettingsEvent()

    data class DisplayCompletedSelected(val displayCompleted: DisplayCompleted) : SettingsEvent()

    object ShowBackScreen : SettingsEvent()

    object ShowNavigationDrawer : SettingsEvent()

    object HideFontSize : SettingsEvent()

    object HideNavigationDrawer : SettingsEvent()

    object HideProductsDisplayAutocomplete : SettingsEvent()

    object HideDisplayCompleted : SettingsEvent()
}