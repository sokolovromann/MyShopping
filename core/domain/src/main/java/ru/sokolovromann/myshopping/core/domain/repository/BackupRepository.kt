package ru.sokolovromann.myshopping.core.domain.repository

import ru.sokolovromann.myshopping.core.domain.model.Backup
import ru.sokolovromann.myshopping.core.domain.model.BackupValue

interface BackupRepository {

    suspend fun exportBackup(backup: Backup): Boolean

    suspend fun importBackup(uriString: String): BackupValue?
}