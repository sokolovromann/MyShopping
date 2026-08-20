package ru.sokolovromann.myshopping.navigation

import ru.sokolovromann.myshopping.core.navigation.Screen

sealed class NavigationAction {

    data class NavigateTo(val screen: Screen) : NavigationAction()

    data object PopBackStack : NavigationAction()
}