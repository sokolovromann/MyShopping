package ru.sokolovromann.myshopping.data39.settings.backup

import ru.sokolovromann.myshopping.data39.LocalEnvironment
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class BackupConfigManager @Inject constructor() {

    suspend fun getConfig(): BackupConfig = withIoContext {
        return@withIoContext BackupConfig(
            location = LocalEnvironment.ABSOLUTE_OLD_ROOT_DIRECTORY
        )
    }
}