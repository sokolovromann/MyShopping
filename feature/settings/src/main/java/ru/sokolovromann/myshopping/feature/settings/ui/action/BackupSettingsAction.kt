package ru.sokolovromann.myshopping.feature.settings.ui.action

import android.net.Uri

sealed class BackupSettingsAction {

    data object OnExportBackupClicked : BackupSettingsAction()

    data class OnImportBackupClicked(val uri: Uri) : BackupSettingsAction()
}