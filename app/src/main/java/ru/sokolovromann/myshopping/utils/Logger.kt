package ru.sokolovromann.myshopping.utils

import android.util.Log

object Logger {

    private const val LOG_TAG: String = "MShLogger"

    fun info(message: String) {
        Log.i(LOG_TAG, message)
    }

    fun error(message: String) {
        Log.e(LOG_TAG, message)
    }

    fun warning(message: String) {
        Log.w(LOG_TAG, message)
    }
}