package ru.sokolovromann.myshopping.ui.utils

import java.util.Locale

fun Locale.isSupported(): Boolean {
    return when (language) {
        "en", "de", "ru", "uk" -> true
        else -> false
    }
}