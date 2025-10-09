package ru.sokolovromann.myshopping.data39.about

import ru.sokolovromann.myshopping.BuildConfig
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class AboutManager @Inject constructor() {

    suspend fun getAbout(): About = withIoContext {
        return@withIoContext About(
            id = BuildConfig.APPLICATION_ID,
            api = BuildConfig.VERSION_CODE.toLong(),
            version = BuildConfig.VERSION_NAME,
            developer = "Roman Sokolov",
            email = "developer.myshopping@gmail.com",
            github = "https://github.com/sokolovromann/MyShopping",
            privacyPolicy = "https://sokolovromann.github.io/MyShopping/privacy_policy.html",
            termsOfConditions = "https://sokolovromann.github.io/MyShopping/terms_and_conditions.html"
        )
    }
}