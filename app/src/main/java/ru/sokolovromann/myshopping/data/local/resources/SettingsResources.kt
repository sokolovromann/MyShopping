package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import ru.sokolovromann.myshopping.R
import javax.inject.Inject

class SettingsResources @Inject constructor(
    private val resources: Resources
) {

    fun getDeveloperName(): String {
        return resources.getString(R.string.data_developerName)
    }

    fun getDeveloperEmail(): String {
        return resources.getString(R.string.data_developerEmail)
    }

    fun getGithubLink(): String {
        return resources.getString(R.string.data_githubLink)
    }
}