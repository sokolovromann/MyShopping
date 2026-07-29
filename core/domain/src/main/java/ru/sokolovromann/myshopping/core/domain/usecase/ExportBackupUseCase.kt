package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository

class ExportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(backup: Backup): Boolean =
        withContext(ioDispatcher) {
            backupRepository.exportBackup(backup)
        }
}