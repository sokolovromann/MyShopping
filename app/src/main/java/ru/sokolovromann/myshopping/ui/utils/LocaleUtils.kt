package ru.sokolovromann.myshopping.ui.utils

import java.util.Locale

@Deprecated("Use AppLocale.isLanguageSupported()")
fun Locale.isSupported(): Boolean {
    return when (language) {
        "en", "de", "ru", "uk" -> true
        else -> false
    }
}