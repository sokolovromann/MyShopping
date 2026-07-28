package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.model.BackupValue
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository

class ImportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(uriString: String): BackupValue? =
        withContext(ioDispatcher) {
            backupRepository.importBackup(uriString)
        }
}