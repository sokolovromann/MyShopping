package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class AboutEvent {

    object OnClickBack : AboutEvent()

    object OnClickEmail : AboutEvent()

    object OnClickGitHub : AboutEvent()

    object OnClickPrivacyPolicy : AboutEvent()

    object OnClickTermsAndConditions : AboutEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : AboutEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : AboutEvent()
}