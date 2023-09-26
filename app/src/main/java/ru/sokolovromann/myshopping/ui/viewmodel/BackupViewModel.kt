package ru.sokolovromann.myshopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.data.repository.AppConfigRepository
import ru.sokolovromann.myshopping.data.repository.AutocompletesRepository
import ru.sokolovromann.myshopping.data.repository.BackupRepository
import ru.sokolovromann.myshopping.data.repository.ShoppingListsRepository
import ru.sokolovromann.myshopping.data.repository.model.AppConfig
import ru.sokolovromann.myshopping.data.repository.model.Backup
import ru.sokolovromann.myshopping.data.repository.model.ShoppingList
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
    private val shoppingListsRepository: ShoppingListsRepository,
    private val autocompletesRepository: AutocompletesRepository,
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

        val backup = backupRepository.createBackup(BuildConfig.VERSION_CODE).firstOrNull()
        if (backup == null) {
            backupState.showExportError()
        } else {
            backupRepository.exportBackup(backup)
                .onSuccess { fileName ->
                    createReminderIfExists(backup.shoppingLists)
                    backupState.showExportSuccessful(fileName)
                }
                .onFailure { backupState.showExportError() }
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
            .onSuccess {
                val backup = it.firstOrNull()
                if (backup == null) {
                    backupState.showImportError()
                } else {
                    transferDataFromBackup(backup)
                }
            }
            .onFailure { backupState.showImportError() }
    }

    private suspend fun transferDataFromBackup(importedBackup: Backup) {
        val appConfig = createAppConfigFromBackup(importedBackup)

        listOf(
            viewModelScope.async { deleteReminders() },
            viewModelScope.async { shoppingListsRepository.deleteAllShoppingLists() },
            viewModelScope.async { shoppingListsRepository.saveShoppingLists(importedBackup.shoppingLists) },
            viewModelScope.async { shoppingListsRepository.saveProducts(importedBackup.products) },
            viewModelScope.async { autocompletesRepository.deleteAllAutocompletes() },
            viewModelScope.async { autocompletesRepository.saveAutocompletes(importedBackup.autocompletes) },
            viewModelScope.async { appConfigRepository.saveAppConfig(appConfig) }
        ).awaitAll()

        backupState.showImportSuccessful()
    }

    private fun createAppConfigFromBackup(backup: Backup): AppConfig {
        val appBuildConfig = backup.appConfig.appBuildConfig.copy(
            userCodeVersion = BuildConfig.VERSION_CODE
        )
        return backup.appConfig.copy(appBuildConfig = appBuildConfig)
    }

    private suspend fun deleteReminders() {
        shoppingListsRepository.getReminders().firstOrNull()?.let { shoppingLists ->
            shoppingLists.getUids().forEach { uid ->
                withContext(dispatchers.main) {
                    alarmManager.deleteReminder(uid)
                }
            }
        }
    }

    private fun createReminderIfExists(shoppingLists: List<ShoppingList>) {
        shoppingLists.forEach { shoppingList ->
            shoppingList.reminder?.let { reminder ->
                alarmManager.createReminder(shoppingList.uid, reminder)
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