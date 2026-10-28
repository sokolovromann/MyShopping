package ru.sokolovromann.myshopping.core.domain.repository

interface Api15BackupManager {

    suspend fun importBackup(uriString: String): Boolean
}