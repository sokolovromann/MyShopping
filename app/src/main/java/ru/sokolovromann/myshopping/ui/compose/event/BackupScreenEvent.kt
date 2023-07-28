package ru.sokolovromann.myshopping.ui.compose.event

sealed class BackupScreenEvent {

    object ShowBackScreen : BackupScreenEvent()

    object ShowSelectFile : BackupScreenEvent()

    data class ShowPermissions(val packageName: String) : BackupScreenEvent()
}
