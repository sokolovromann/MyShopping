package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.AppPreferences
import ru.sokolovromann.myshopping.data.repository.model.FontSize

class BackupState {

    var screenData by mutableStateOf(BackupScreenData())
        private set

    init {
        screenData = BackupScreenData(screenState = ScreenState.Showing)
    }

    fun onCreate(preferences: AppPreferences, correctWriteFilesPermission: Boolean) {
        screenData = BackupScreenData(
            screenState = ScreenState.Showing,
            showPermissionError = !correctWriteFilesPermission,
            fontSize = preferences.fontSize
        )
    }

    fun showExportSuccessful(fileName: String) {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_exportSuccessful),
            locationText = UiText.FromResourcesWithArgs(R.string.backup_message_exportLocation, fileName)
        )
    }

    fun showExportError() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_exportError)
        )
    }

    fun showExportProgress() {
        screenData = screenData.copy(screenState = ScreenState.Saving)
    }

    fun showImportSuccessful() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_importSuccessful)
        )
    }

    fun showImportError() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_importError)
        )
    }

    fun showImportProgress() {
        screenData = screenData.copy(screenState = ScreenState.Loading)
    }
}

data class BackupScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val messageText: UiText = UiText.Nothing,
    val locationText: UiText = UiText.Nothing,
    val showPermissionError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)