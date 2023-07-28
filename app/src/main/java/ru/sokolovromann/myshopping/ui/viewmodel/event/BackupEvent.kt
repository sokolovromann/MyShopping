package ru.sokolovromann.myshopping.ui.viewmodel.event

import android.net.Uri

sealed class BackupEvent {

    object Export : BackupEvent()

    object SelectFile : BackupEvent()

    data class Import(val uri: Uri) : BackupEvent()

    object ShowBackScreen : BackupEvent()

    object ShowPermissions : BackupEvent()
}
