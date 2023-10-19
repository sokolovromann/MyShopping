package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.FontSize

class BackupState {

    var screenData by mutableStateOf(BackupScreenData())
        private set

    init {
        screenData = BackupScreenData(screenState = ScreenState.Showing)
    }

    fun onCreate(appConfig: AppConfig, correctWriteFilesPermission: Boolean) {
        screenData = BackupScreenData(
            screenState = ScreenState.Showing,
            showPermissionError = !correctWriteFilesPermission,
            fontSize = appConfig.userPreferences.fontSize
        )
    }

    fun showExportSuccessful(fileName: String) {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_exportSuccessful),
            locationText = UiText.FromResourcesWithArgs(R.string.backup_message_exportLocation, fileName),
            warningText = UiText.FromResources(R.string.backup_message_warning)
        )
    }

    fun showExportError() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_exportError),
            warningText = UiText.Nothing
        )
    }

    fun showExportProgress() {
        screenData = screenData.copy(screenState = ScreenState.Saving)
    }

    fun showImportSuccessful() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_importSuccessful),
            warningText = UiText.Nothing
        )
    }

    fun showImportError() {
        screenData = screenData.copy(
            screenState = ScreenState.Showing,
            messageText = UiText.FromResources(R.string.backup_message_importError),
            warningText = UiText.Nothing
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
    val warningText: UiText = UiText.Nothing,
    val showPermissionError: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)