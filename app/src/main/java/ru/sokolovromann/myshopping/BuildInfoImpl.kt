package ru.sokolovromann.myshopping

import android.content.Context
import android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo

class BuildInfoImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BuildInfo {

    override fun getPackageName() = context.packageName.orEmpty()

    override fun getApi() = API(context.getString(R.string.app_version_code).toLong())

    override fun getApiName() = "${context.getString(R.string.app_version_name)} (API ${getApi().value})"

    override fun isDebug() = (context.applicationInfo.flags and FLAG_DEBUGGABLE) != 0
}