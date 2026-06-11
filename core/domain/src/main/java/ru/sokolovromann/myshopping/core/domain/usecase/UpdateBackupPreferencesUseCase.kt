package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.repository.UserPreferencesRepository

class UpdateBackupPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val observeBackupPreferencesUseCase: ObserveBackupPreferencesUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(preferences: BackupPreferences): Unit = withContext(ioDispatcher) {
        userPreferencesRepository.updateBackupPreferences(preferences)
    }

    suspend operator fun invoke(directory: BackupDirectory): Unit = withContext(ioDispatcher) {
        val preferences = getPreferences().copy(directory = directory)
        userPreferencesRepository.updateBackupPreferences(preferences)
    }

    private suspend fun getPreferences() = observeBackupPreferencesUseCase().first()
}