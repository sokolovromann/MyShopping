package ru.sokolovromann.myshopping.data39

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.StringRes
import javax.inject.Inject

class LocalResources @Inject constructor(private val context: Context) {

    fun getStrings(@ArrayRes id: Int): Collection<String> {
        return context.resources.getStringArray(id).toList()
    }

    fun getString(@StringRes id: Int): String {
        return context.resources.getString(id)
    }

    fun getBoolean(@BoolRes id: Int): Boolean {
        return context.resources.getBoolean(id)
    }
}