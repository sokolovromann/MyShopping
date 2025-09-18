package ru.sokolovromann.myshopping.about

data class AboutEntity(
    val id: String,
    val api: Long,
    val version: String,
    val developer: String?,
    val email: String?,
    val linkedin: String?,
    val github: String?,
    val privacyPolicy: String?,
    val termsOfConditions: String?
)