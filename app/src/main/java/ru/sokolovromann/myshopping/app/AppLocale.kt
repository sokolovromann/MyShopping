package ru.sokolovromann.myshopping.app

import java.util.Locale

object AppLocale {

    private val supportedLanguages = listOf("en", "de", "es", "fr", "ru", "uk")

    fun getCurrentLanguage(): String {
        return getDefault().language
    }

    fun isLanguageSupported(): Boolean {
        return supportedLanguages.contains(getCurrentLanguage())
    }

    private fun getDefault(): Locale {
        return Locale.getDefault()
    }
}