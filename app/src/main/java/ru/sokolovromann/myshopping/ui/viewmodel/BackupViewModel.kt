package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.BuildConfig
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
    private val dispatchers: AppDispatchers,
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
    ) = withContext(dispatchers.main) {
        backupState.onCreate(
            appConfig = appConfig,
            correctWriteFilesPermission = mediaStore.checkCorrectWriteFilesPermissions()
        )
    }

    private fun export() = viewModelScope.launch {
        withContext(dispatchers.main) {
            backupState.showExportProgress()
        }

        backupRepository.exportBackup(BuildConfig.VERSION_CODE)
            .onSuccess { backup ->
                withContext(dispatchers.main) {
                    backupState.showExportSuccessful(backup.fileName)
                }
            }
            .onFailure {
                withContext(dispatchers.main) {
                    backupState.showExportError()
                }
            }
    }

    private fun selectFile() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(BackupScreenEvent.ShowSelectFile)
    }

    private fun import(event: BackupEvent.Import) = viewModelScope.launch {
        withContext(dispatchers.main) {
            backupState.showImportProgress()
        }

        backupRepository.importBackup(event.uri)
            .onSuccess { oldNewBackups ->
                withContext(dispatchers.main) {
                    deleteRemindersIfExists(oldNewBackups.first.shoppings)
                    createReminderIfExists(oldNewBackups.second.shoppings)
                    backupState.showImportSuccessful()
                }
            }
            .onFailure {
                withContext(dispatchers.main) {
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

    private fun showBackScreen() = viewModelScope.launch(dispatchers.main) {
        _screenEventFlow.emit(BackupScreenEvent.ShowBackScreen)
    }

    private fun showPermissions() = viewModelScope.launch(dispatchers.main) {
        val event = BackupScreenEvent.ShowPermissions(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }
}