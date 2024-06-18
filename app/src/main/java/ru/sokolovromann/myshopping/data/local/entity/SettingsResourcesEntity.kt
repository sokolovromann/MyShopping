package ru.sokolovromann.myshopping.data.local.entity

@Deprecated("")
data class SettingsResourcesEntity(
    val developerName: String = "",
    val developerEmail: String = "",
    val appGithubLink: String = "",
    val privacyPolicyLink: String = "",
    val termsAndConditionsLink: String = ""
)