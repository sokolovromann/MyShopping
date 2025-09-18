package ru.sokolovromann.myshopping.about

import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class GetAboutUseCase @Inject constructor(
    private val aboutDao: AboutDao,
    private val aboutMapper: AboutMapper
) {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend operator fun invoke(): About = withContext(dispatcher) {
        val aboutEntity = aboutDao.getAbout()
        return@withContext aboutMapper.mapTo(aboutEntity)
    }
}