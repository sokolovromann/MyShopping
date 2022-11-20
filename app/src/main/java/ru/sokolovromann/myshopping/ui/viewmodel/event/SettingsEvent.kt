package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.SettingsUid

sealed class SettingsEvent {

    data class SelectSettingsItem(val uid: SettingsUid) : SettingsEvent()

    data class SelectNavigationItem(val route: UiRoute) : SettingsEvent()

    object DisplayProductsAllAutocomplete : SettingsEvent()

    object DisplayProductsNameAutocomplete : SettingsEvent()

    object TinyFontSizeSelected : SettingsEvent()

    object SmallFontSizeSelected : SettingsEvent()

    object MediumFontSizeSelected : SettingsEvent()

    object LargeFontSizeSelected : SettingsEvent()

    object HugeFontSizeSelected : SettingsEvent()

    object ShowBackScreen : SettingsEvent()

    object ShowNavigationDrawer : SettingsEvent()

    object HideFontSize : SettingsEvent()

    object HideNavigationDrawer : SettingsEvent()

    object HideProductsAutocomplete : SettingsEvent()

    object HideProductsDisplayAutocomplete : SettingsEvent()
}