package ru.sokolovromann.myshopping.ui.compose.event

sealed class BackupScreenEvent {

    object OnShowBackScreen : BackupScreenEvent()

    data class OnShowPermissionsScreen(val packageName: String) : BackupScreenEvent()

    object OnSelectFile : BackupScreenEvent()
}
