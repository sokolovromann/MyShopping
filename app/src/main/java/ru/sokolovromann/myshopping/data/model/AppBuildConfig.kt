package ru.sokolovromann.myshopping.data.model

import ru.sokolovromann.myshopping.BuildConfig

data class AppBuildConfig(
    val appId: String = BuildConfig.APPLICATION_ID,
    val appVersionName: String = BuildConfig.VERSION_NAME,
    val userCodeVersion: Int = UNKNOWN_CODE_VERSION,
    val appCodeVersion: Int = BuildConfig.VERSION_CODE
) {

    companion object {
        const val UNKNOWN_CODE_VERSION = 0
        const val CODE_VERSION_14 = 14
        const val CODE_VERSION_18 = 18
    }

    fun getDisplayName(): String {
        return if (isDebug()) {
            "Debug"
        } else {
            "$appVersionName ($appCodeVersion)"
        }
    }

    fun getOpenHelper(): AppOpenHelper {
        return if (userCodeVersion <= UNKNOWN_CODE_VERSION) {
            AppOpenHelper.Create
        } else {
            if (userCodeVersion < appCodeVersion) {
                AppOpenHelper.Migrate
            } else if (userCodeVersion == appCodeVersion) {
                AppOpenHelper.Open
            } else {
                val exception = IllegalArgumentException(
                    "User code version must not be greater than app code version"
                )
                AppOpenHelper.Error(exception)
            }
        }
    }

    fun isLoggingEnabled(): Boolean {
        return isDebug()
    }

    private fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}