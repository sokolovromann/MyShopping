package ru.sokolovromann.myshopping.core.domain.repository

import ru.sokolovromann.myshopping.core.domain.model.Backup

interface BackupRepository {

    suspend fun exportBackup(backup: Backup): Boolean

    suspend fun importBackup(uriString: String): Backup?
}