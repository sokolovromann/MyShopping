package ru.sokolovromann.myshopping.core.navigation

interface Navigator {

    fun navigateTo(screen: Screen)

    fun navigateBack()
}