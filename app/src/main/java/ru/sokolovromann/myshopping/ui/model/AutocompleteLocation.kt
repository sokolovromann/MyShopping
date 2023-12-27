package ru.sokolovromann.myshopping.ui.model

enum class AutocompleteLocation {

    DEFAULT, PERSONAL;

    companion object {
        val DefaultValue: AutocompleteLocation = PERSONAL
    }
}