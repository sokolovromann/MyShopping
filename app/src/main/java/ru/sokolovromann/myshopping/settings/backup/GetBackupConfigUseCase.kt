package ru.sokolovromann.myshopping.settings.backup

import ru.sokolovromann.myshopping.io.LocalEnvironment
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class GetBackupConfigUseCase @Inject constructor(
    private val dispatcher: Dispatcher
) {

    suspend operator fun invoke(): BackupConfig = withContext(dispatcher) {
        return@withContext BackupConfig(
            location = LocalEnvironment.ABSOLUTE_OLD_ROOT_DIRECTORY
        )
    }
}