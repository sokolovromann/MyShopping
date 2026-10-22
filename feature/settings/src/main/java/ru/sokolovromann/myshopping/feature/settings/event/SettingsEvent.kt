package ru.sokolovromann.myshopping.feature.settings.event

sealed class SettingsEvent {

    data object ShowExportBackupSuccessMessage : SettingsEvent()

    data object ShowImportBackupSuccessMessage : SettingsEvent()

    data object ShowBackupFailureMessage : SettingsEvent()

}