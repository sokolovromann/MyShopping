package ru.sokolovromann.myshopping.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(value = "backup_info")
data class BackupInfoEntity(
    val api: String
) : Backupable