package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.R

enum class AutocompleteLocation {

    DEFAULT, PERSONAL;

    companion object {
        val DefaultValue: AutocompleteLocation = PERSONAL
    }

    fun getText(): UiText = when (this) {
        DEFAULT -> UiText.FromResources(R.string.autocompletes_action_selectDefaultLocation)
        PERSONAL -> UiText.FromResources(R.string.autocompletes_action_selectPersonalLocation)
    }
}