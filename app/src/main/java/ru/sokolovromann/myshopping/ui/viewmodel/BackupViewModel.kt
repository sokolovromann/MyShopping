package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.BackupRepository
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.media.BackupMediaStore
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.compose.event.BackupScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.BackupState
import ru.sokolovromann.myshopping.ui.viewmodel.event.BackupEvent
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val appConfigRepository: AppConfigRepository,
    private val alarmManager: PurchasesAlarmManager,
    private val mediaStore: BackupMediaStore
): ViewModel(), ViewModelEvent<BackupEvent> {

    val backupState = BackupState()

    private val _screenEventFlow: MutableSharedFlow<BackupScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<BackupScreenEvent> = _screenEventFlow

    init {
        getAppConfig()
    }

    override fun onEvent(event: BackupEvent) {
        when (event) {
            BackupEvent.Export -> export()

            is BackupEvent.Import -> import(event)

            BackupEvent.SelectFile -> selectFile()

            BackupEvent.ShowBackScreen -> showBackScreen()

            BackupEvent.ShowPermissions -> showPermissions()
        }
    }

    private fun getAppConfig() = viewModelScope.launch {
        appConfigRepository.getAppConfig().collect {
            appConfigLoaded(it)
        }
    }

    private suspend fun appConfigLoaded(
        appConfig: AppConfig
    ) = withContext(AppDispatchers.Main) {
        backupState.onCreate(
            appConfig = appConfig,
            correctWriteFilesPermission = mediaStore.checkCorrectWriteFilesPermissions()
        )
    }

    private fun export() = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            backupState.showExportProgress()
        }

        backupRepository.exportBackup(BuildConfig.VERSION_CODE)
            .onSuccess { backup ->
                withContext(AppDispatchers.Main) {
                    backupState.showExportSuccessful(backup.fileName)
                }
            }
            .onFailure {
                withContext(AppDispatchers.Main) {
                    backupState.showExportError()
                }
            }
    }

    private fun selectFile() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(BackupScreenEvent.ShowSelectFile)
    }

    private fun import(event: BackupEvent.Import) = viewModelScope.launch {
        withContext(AppDispatchers.Main) {
            backupState.showImportProgress()
        }

        backupRepository.importBackup(event.uri)
            .onSuccess { oldNewBackups ->
                withContext(AppDispatchers.Main) {
                    deleteRemindersIfExists(oldNewBackups.first.shoppings)
                    createReminderIfExists(oldNewBackups.second.shoppings)
                    backupState.showImportSuccessful()
                }
            }
            .onFailure {
                withContext(AppDispatchers.Main) {
                    backupState.showImportError()
                }
            }
    }

    private fun deleteRemindersIfExists(shoppings: List<Shopping>) {
        shoppings.forEach { shopping ->
            if (shopping.reminder != null) {
                alarmManager.deleteReminder(shopping.uid)
            }
        }
    }

    private fun createReminderIfExists(shoppings: List<Shopping>) {
        shoppings.forEach { shopping ->
            shopping.reminder?.let { dateTime ->
                alarmManager.createReminder(shopping.uid, dateTime.millis)
            }
        }
    }

    private fun showBackScreen() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(BackupScreenEvent.ShowBackScreen)
    }

    private fun showPermissions() = viewModelScope.launch(AppDispatchers.Main) {
        val event = BackupScreenEvent.ShowPermissions(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }
}