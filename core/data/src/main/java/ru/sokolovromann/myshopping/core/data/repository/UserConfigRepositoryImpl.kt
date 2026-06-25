package ru.sokolovromann.myshopping.core.data.repository

import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStore
import ru.sokolovromann.myshopping.core.data.mapper.UserConfigMapper
import ru.sokolovromann.myshopping.core.domain.model.UserConfig
import ru.sokolovromann.myshopping.core.domain.repository.UserConfigRepository

class UserConfigRepositoryImpl(
    private val localDataStore: LocalDataStore,
    private val userConfigMapper: UserConfigMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserConfigRepository {

    override fun observeUserConfig(): Flow<UserConfig> =
        localDataStore.getUserConfig().data
            .map { userConfigMapper.toModel(it) }
            .flowOn(ioDispatcher)

    override suspend fun updateUserConfig(config: UserConfig): Unit =
        withContext(ioDispatcher) {
            localDataStore.getUserConfig().edit {
                val newPreferences = userConfigMapper.toPreferences(config)
                it.plusAssign(newPreferences)
            }
        }
}