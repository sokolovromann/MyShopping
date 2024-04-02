package ru.sokolovromann.myshopping.data.model

enum class NightTheme {

    DISABLED, APP, WIDGET, APP_AND_WIDGET;

    companion object {
        val DefaultValue: NightTheme = DISABLED

        fun valueOfOrDefault(appNightTheme: Boolean?, widgetNightTheme: Boolean?): NightTheme = try {
            if (appNightTheme == true) {
                if (widgetNightTheme == true) APP_AND_WIDGET else APP
            } else {
                if (widgetNightTheme == true) WIDGET else DISABLED
            }
        } catch (e: Exception) {
            DefaultValue
        }
    }

    fun isAppNightTheme(): Boolean {
        return this == APP || this == APP_AND_WIDGET
    }

    fun isWidgetNightTheme(): Boolean {
        return this == WIDGET || this == APP_AND_WIDGET
    }

    override fun toString(): String {
        return name
    }
}