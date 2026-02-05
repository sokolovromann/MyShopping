package ru.sokolovromann.myshopping.utils

import androidx.lifecycle.SavedStateHandle

object SavedStateHandleExtensions {

    fun SavedStateHandle.getUid(key: String): UID {
        val value = get<String>(key).orEmpty()
        return UID(value)
    }
}