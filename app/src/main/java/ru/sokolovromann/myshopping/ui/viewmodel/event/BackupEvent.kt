package ru.sokolovromann.myshopping.ui.viewmodel.event

import android.net.Uri

sealed class BackupEvent {

    object OnClickCancel : BackupEvent()

    object OnClickOpenPermissions : BackupEvent()

    object OnClickExport : BackupEvent()

    object OnClickImport : BackupEvent()

    data class OnFileSelected(val uri: Uri) : BackupEvent()
}
