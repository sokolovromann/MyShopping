package ru.sokolovromann.myshopping.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.domain.model.UserConfig

interface UserConfigRepository {

    fun observeUserConfig(): Flow<UserConfig>

    suspend fun updateUserConfig(config: UserConfig)
}