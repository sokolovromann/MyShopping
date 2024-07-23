package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiString {

    data class FromString(val value: String) : UiString()

    data class FromResources(@StringRes val id: Int, val lowercase: Boolean = false) : UiString()

    class FromResourcesWithArgs(@StringRes val id: Int, vararg val args: Any, val lowercase: Boolean = false) : UiString()

    data class FromResourcesWithUiString(@StringRes val id: Int, val str: UiString, val separator: UiString, val lowercase: Boolean = false) : UiString()

    class FromUiStrings(val strs: Array<UiString>, val separator: UiString) : UiString()

    @Composable
    fun asCompose(): String = when (this) {
        is FromString -> value
        is FromResources -> {
            if (lowercase) {
                stringResource(id).lowercase()
            } else {
                stringResource(id)
            }
        }
        is FromResourcesWithArgs -> {
            if (lowercase) {
                stringResource(id, *args).lowercase()
            } else {
                stringResource(id, *args)
            }
        }
        is FromResourcesWithUiString -> {
            val text = StringBuilder().apply {
                append(stringResource(id))
                append(separator.asCompose())
                append(str.asCompose())
            }.toString()

            if (lowercase) {
                text.lowercase()
            } else {
                text
            }
        }
        is FromUiStrings -> {
            StringBuilder().apply {
                strs.forEachIndexed { index, str ->
                    append(str.asCompose())
                    if (index < strs.lastIndex) {
                        append(separator.asCompose())
                    }
                }
            }.toString()
        }
    }

    @Composable
    fun isEmpty(): Boolean {
        return this.asCompose().isEmpty()
    }

    @Composable
    fun isNotEmpty(): Boolean {
        return this.asCompose().isNotEmpty()
    }
}