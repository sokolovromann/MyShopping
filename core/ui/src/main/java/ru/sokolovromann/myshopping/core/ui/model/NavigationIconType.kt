package ru.sokolovromann.myshopping.core.ui.model

sealed class NavigationIconType {

    data object Menu : NavigationIconType()

    data object Back : NavigationIconType()

    data object Close : NavigationIconType()

    data class Other(val icon: UiIcon, val description: String) : NavigationIconType()
}