package ru.sokolovromann.myshopping.feature.settings.ui.event

sealed class SettingsEvent {

    data object ShowExportBackupSuccessMessage : SettingsEvent()

    data object ShowImportBackupSuccessMessage : SettingsEvent()

    data object ShowBackupFailureMessage : SettingsEvent()
}