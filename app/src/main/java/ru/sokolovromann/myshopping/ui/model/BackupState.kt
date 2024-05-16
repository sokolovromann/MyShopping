package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig

class BackupState {

    var messageText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var locationText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var warningText: UiString by mutableStateOf(UiString.FromString(""))
        private set

    var permissionError: Boolean by mutableStateOf(false)
        private set

    var displayBack: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig, correctWriteFilesPermission: Boolean) {
        messageText = UiString.FromResources(R.string.backup_text_exportImport)
        locationText = UiString.FromString("")
        warningText = UiString.FromString("")
        permissionError = !correctWriteFilesPermission
        displayBack = false
        waiting = false
    }

    fun onShowExportMessage(success: Boolean, fileName: String?) {
        if (success) {
            messageText = UiString.FromResources(R.string.backup_message_exportSuccessful)
            locationText = UiString.FromResourcesWithArgs(R.string.backup_message_exportLocation, fileName ?: "")
            warningText = UiString.FromResources(R.string.backup_message_warning)
        } else {
            messageText = UiString.FromResources(R.string.backup_message_exportError)
            locationText = UiString.FromString("")
            warningText = UiString.FromString("")
        }
        displayBack = true
        waiting = false
    }

    fun onShowImportMessage(success: Boolean) {
        messageText = if (success) {
            UiString.FromResources(R.string.backup_message_importSuccessful)
        } else {
            UiString.FromResources(R.string.backup_message_importError)
        }
        locationText = UiString.FromString("")
        warningText = UiString.FromString("")
        displayBack = true
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }
}