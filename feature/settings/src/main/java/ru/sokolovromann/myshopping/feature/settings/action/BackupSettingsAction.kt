package ru.sokolovromann.myshopping.feature.settings.action

import android.net.Uri

sealed class BackupSettingsAction {

    data object OnExportBackupClicked : BackupSettingsAction()

    data class OnImportBackupClicked(val uri: Uri) : BackupSettingsAction()
}