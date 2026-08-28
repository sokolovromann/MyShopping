package ru.sokolovromann.myshopping

import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo

class BuildInfoImpl : BuildInfo {

    override fun getPackageName() = BuildConfig.APPLICATION_ID

    override fun getApi() = API(BuildConfig.VERSION_CODE.toLong())

    override fun getApiName() = "${BuildConfig.VERSION_NAME} (API ${BuildConfig.VERSION_CODE})"

    override fun isDebug() = BuildConfig.DEBUG
}