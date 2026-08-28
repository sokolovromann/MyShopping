package ru.sokolovromann.myshopping.core.domain.repository

import ru.sokolovromann.myshopping.core.domain.model.API

interface BuildInfo {

    fun getPackageName(): String

    fun getApi(): API

    fun getApiName(): String

    fun isDebug(): Boolean
}