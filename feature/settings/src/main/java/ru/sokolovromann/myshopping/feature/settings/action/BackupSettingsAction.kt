package ru.sokolovromann.myshopping.feature.settings.action

import java.net.URI

sealed class BackupSettingsAction {

    data object OnExportBackupClicked : BackupSettingsAction()

    data class OnImportBackupClicked(val uri: URI) : BackupSettingsAction()
}