package ru.sokolovromann.myshopping.data.local.resources

import android.content.res.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.local.entity.SettingsResourcesEntity
import javax.inject.Inject

class SettingsResources @Inject constructor(
    private val resources: Resources
) {

    fun getSettingsResources(): Flow<SettingsResourcesEntity> {
        val entity = SettingsResourcesEntity(
            developerName = resources.getString(R.string.data_developerName),
            developerEmail = resources.getString(R.string.data_developerEmail),
            appGithubLink = resources.getString(R.string.data_githubLink)
        )
        return flowOf(entity)
    }
}