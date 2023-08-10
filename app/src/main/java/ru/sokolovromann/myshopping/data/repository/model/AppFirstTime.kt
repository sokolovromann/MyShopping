package ru.sokolovromann.myshopping.data.repository.model

@Deprecated("")
enum class AppFirstTime {

    NOTHING, FIRST_TIME, FIRST_TIME_FROM_APP_VERSION_14;

    companion object {
        val DefaultValue: AppFirstTime = FIRST_TIME

        fun valueOfOrDefault(value: String): AppFirstTime = try {
            valueOf(value)
        } catch (e: Exception) {
            DefaultValue
        }
    }
}