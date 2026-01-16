package ru.sokolovromann.myshopping.data39.suggestions

enum class SuggestionDirectory {

    PreInstalled,

    Personal;

    override fun toString(): String {
        return name
    }
}