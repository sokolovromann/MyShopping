package ru.sokolovromann.myshopping.core.domain.model

data class Backup(
    val directory: BackupDirectory,
    val value: BackupValue
)