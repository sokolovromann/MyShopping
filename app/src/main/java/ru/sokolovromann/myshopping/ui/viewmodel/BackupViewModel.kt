package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.app.AppDispatchers
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.BackupRepository
import ru.sokolovromann.myshopping.data.model.Shopping
import ru.sokolovromann.myshopping.media.BackupMediaStore
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.ui.compose.event.BackupScreenEvent
import ru.sokolovromann.myshopping.ui.model.BackupState
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

    private fun onInit() = viewModelScope.launch(AppDispatchers.Main) {
        appConfigRepository.getAppConfig().collect {
            backupState.populate(it, mediaStore.checkCorrectWriteFilesPermissions())
        }
    }

    private fun onClickCancel() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(BackupScreenEvent.OnShowBackScreen)
    }

    private fun onClickOpenPermissions() = viewModelScope.launch(AppDispatchers.Main) {
        val event = BackupScreenEvent.OnShowPermissionsScreen(BuildConfig.APPLICATION_ID)
        _screenEventFlow.emit(event)
    }

    private fun onClickExport() = viewModelScope.launch(AppDispatchers.Main) {
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

    private fun onClickImport() = viewModelScope.launch(AppDispatchers.Main) {
        _screenEventFlow.emit(BackupScreenEvent.OnSelectFile)
    }

    private fun onFileSelected(
        event: BackupEvent.OnFileSelected
    ) = viewModelScope.launch(AppDispatchers.Main) {
        backupState.onWaiting()

        backupRepository.importBackup(event.uri)
            .onSuccess {
                deleteRemindersIfExists(it.first.shoppings)
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