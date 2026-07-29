package ru.sokolovromann.myshopping.core.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.sokolovromann.myshopping.core.domain.di.IoDispatcher
import ru.sokolovromann.myshopping.core.domain.model.UserConfig
import ru.sokolovromann.myshopping.core.domain.repository.UserConfigRepository

class ObserveUserConfigUseCase @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<UserConfig> =
        userConfigRepository.observeUserConfig().flowOn(ioDispatcher)
}