package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class AboutScreenEvent {

    object OnShowBackScreen : AboutScreenEvent()

    object OnSendEmailToDeveloper : AboutScreenEvent()

    object OnShowAppGithub : AboutScreenEvent()

    object OnShowPrivacyPolicy : AboutScreenEvent()

    object OnShowTermsAndConditions : AboutScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : AboutScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : AboutScreenEvent()
}