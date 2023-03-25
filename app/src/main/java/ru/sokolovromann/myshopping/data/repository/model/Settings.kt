package ru.sokolovromann.myshopping.data.repository.model

data class Settings(
    val developerName: String = "",
    val developerEmail: String = "",
    val appVersion: String = "",
    val appGithubLink: String = "",
    val privacyPolicyLink: String = "",
    val termsAndConditionsLink: String = "",
    val preferences: AppPreferences = AppPreferences()
)