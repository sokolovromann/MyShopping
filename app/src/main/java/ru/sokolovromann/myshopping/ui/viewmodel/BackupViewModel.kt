package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.repository.BackupRepository
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.manager.MigrationManager
import ru.sokolovromann.myshopping.media.BackupMediaStore
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.compose.event.BackupScreenEvent
import ru.sokolovromann.myshopping.ui.model.BackupState
import ru.sokolovromann.myshopping.ui.viewmodel.event.BackupEvent
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val migrationManager: MigrationManager,
    private val alarmManager: PurchasesAlarmManager,
    private val mediaStore: BackupMediaStore
): ViewModel(), ViewModelEvent<BackupEvent> {

    val backupState = BackupState()

    private val _screenEventFlow: MutableSharedFlow<BackupScreenEvent> = MutableSharedFlow()
    val screenEventFlow: SharedFlow<BackupScreenEvent> = _screenEventFlow

    private val dispatcher = Dispatcher.Main

    init { onInit() }

    override fun onEvent(event: BackupEvent) {
        when (event) {
            BackupEvent.OnClickCancel -> onClickCancel()

            BackupEvent.OnClickOpenPermissions -> onClickOpenPermissions()

            BackupEvent.OnClickExport -> onClickExport()

            BackupEvent.OnClickImport -> onClickImport()

            is BackupEvent.OnFileSelected -> onFileSelected(event)
        }
    }

    private fun onInit() = viewModelScope.launch(dispatcher) {
        backupState.populate(mediaStore.checkCorrectWriteFilesPermissions())
    }

    private fun onClickCancel() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(BackupScreenEvent.OnShowBackScreen)
    }

    private fun onClickOpenPermissions() = viewModelScope.launch(dispatcher) {
        val event = BackupScreenEvent.OnShowPermissionsScreen(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }

    private fun onClickExport() = viewModelScope.launch(dispatcher) {
        backupState.onWaiting()

        backupRepository.exportBackup(BuildConfig.VERSION_CODE)
            .onSuccess {
                backupState.onShowExportMessage(
                    success = true,
                    fileName = it.fileName
                )
            }
            .onFailure {
                backupState.onShowExportMessage(
                    success = false,
                    fileName = null
                )
            }
    }

    private fun onClickImport() = viewModelScope.launch(dispatcher) {
        _screenEventFlow.emit(BackupScreenEvent.OnSelectFile)
    }

    private fun onFileSelected(
        event: BackupEvent.OnFileSelected
    ) = viewModelScope.launch(dispatcher) {
        backupState.onWaiting()

        backupRepository.importBackup(event.uri)
            .onSuccess {
                deleteRemindersIfExists(it.first.shoppings)
                migrationManager.migrateAutocompletesFromApi15()
                backupState.onShowImportMessage(success = true)
            }
            .onFailure {
                backupState.onShowImportMessage(success = false)
            }
    }

    private fun deleteRemindersIfExists(shoppings: List<Shopping>) {
        shoppings.forEach { shopping ->
            if (shopping.reminder != null) {
                alarmManager.deleteReminder(shopping.uid)
            }
        }
    }
}