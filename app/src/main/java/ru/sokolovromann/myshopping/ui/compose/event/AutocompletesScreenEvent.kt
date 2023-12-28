package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class AutocompletesScreenEvent {

    object OnShowBackScreen : AutocompletesScreenEvent()

    object OnShowAddAutocomplete : AutocompletesScreenEvent()

    data class OnShowEditAutocomplete(val uid: String) : AutocompletesScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : AutocompletesScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : AutocompletesScreenEvent()
}