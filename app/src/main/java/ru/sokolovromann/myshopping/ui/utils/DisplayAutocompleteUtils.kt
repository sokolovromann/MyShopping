package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun DisplayAutocomplete.toSettingsText(): UiText = when (this) {
    DisplayAutocomplete.ALL -> UiText.FromResources(R.string.settings_action_displayAllAutocomplete)
    DisplayAutocomplete.NAME -> UiText.FromResources(R.string.settings_action_displayNameAutocomplete)
    DisplayAutocomplete.HIDE -> UiText.FromResources(R.string.settings_action_selectHideAutocomplete)
}