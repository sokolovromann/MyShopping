package ru.sokolovromann.myshopping.data.repository.model

enum class AppOpenedAction {

    NOTHING, ADD_DEFAULT_DATA, MIGRATE_FROM_APP_VERSION_14;

    companion object {
        val DefaultValue: AppOpenedAction = NOTHING

        fun valueOfOrDefault(value: String): AppOpenedAction = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}