package ru.sokolovromann.myshopping.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppBuildConfigEntity(
    val appFirstTime: String? = null,
    val codeVersion14: Boolean? = null,
    val userCodeVersion: Int? = null
)