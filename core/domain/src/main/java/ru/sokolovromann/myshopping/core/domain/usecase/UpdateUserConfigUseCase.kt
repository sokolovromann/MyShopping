package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.model.UserConfig
import ru.sokolovromann.myshopping.core.domain.repository.UserConfigRepository

class UpdateUserConfigUseCase @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    private val observeUserConfigUseCase: ObserveUserConfigUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(config: UserConfig): Unit = withContext(ioDispatcher) {
        userConfigRepository.updateUserConfig(config)
    }

    suspend operator fun invoke(api: API): Unit = withContext(ioDispatcher) {
        val config = getUserConfig().copy(api = api)
        userConfigRepository.updateUserConfig(config)
    }

    private suspend fun getUserConfig() = observeUserConfigUseCase().first()
}