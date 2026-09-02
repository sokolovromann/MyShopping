package ru.sokolovromann.myshopping

import ru.sokolovromann.myshopping.core.navigation.Screen

sealed class NavigationAction {

    data class NavigateTo(val screen: Screen) : NavigationAction()

    data object NavigateBack : NavigationAction()

    data object Finish : NavigationAction()
}