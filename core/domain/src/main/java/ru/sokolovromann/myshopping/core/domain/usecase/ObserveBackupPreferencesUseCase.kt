package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.repository.BackupPreferencesRepository

class ObserveBackupPreferencesUseCase @Inject constructor(
    private val backupPreferencesRepository: BackupPreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<BackupPreferences> =
        backupPreferencesRepository.observeBackupPreferences().flowOn(ioDispatcher)
}