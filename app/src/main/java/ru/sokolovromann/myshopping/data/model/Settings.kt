package ru.sokolovromann.myshopping.data.model

data class Settings(
    val developerName: String = "",
    val developerEmail: String = "",
    val appVersion: String = "",
    val appGithubLink: String = "",
    val privacyPolicyLink: String = "",
    val termsAndConditionsLink: String = "",
)