package ru.sokolovromann.myshopping.data.repository.model

data class MainPreferences(
    val appOpenedAction: AppOpenedAction = AppOpenedAction.DefaultValue,
    val nightTheme: Boolean = false
)