package ru.sokolovromann.myshopping.about

import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.utils.Dispatcher
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withContext
import javax.inject.Inject

class AboutDao @Inject constructor() {

    private val dispatcher: Dispatcher = Dispatcher.IO

    suspend fun getAbout(): AboutEntity = withContext(dispatcher) {
        return@withContext AboutEntity(
            id = BuildConfig.APPLICATION_ID,
            api = BuildConfig.VERSION_CODE.toLong(),
            version = BuildConfig.VERSION_NAME,
            developer = "Roman Sokolov",
            email = "developer.myshopping@gmail.com",
            linkedin = null,
            github = "https://github.com/sokolovromann/MyShopping",
            privacyPolicy = "https://sokolovromann.github.io/MyShopping/privacy_policy.html",
            termsOfConditions = "https://sokolovromann.github.io/MyShopping/terms_and_conditions.html"
        )
    }
}