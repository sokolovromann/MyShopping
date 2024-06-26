package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiString {

    data class FromString(val value: String) : UiString()

    data class FromResources(@StringRes val id: Int) : UiString()

    class FromResourcesWithArgs(@StringRes val id: Int, vararg val args: Any) : UiString()

    data class FromResourcesWithUiString(@StringRes val id: Int, val str: UiString, val separator: UiString) : UiString()

    @Composable
    fun asCompose(): String = when (this) {
        is FromString -> value
        is FromResources -> stringResource(id)
        is FromResourcesWithArgs -> stringResource(id, *args)
        is FromResourcesWithUiString -> {
            StringBuilder().apply {
                append(stringResource(id))
                append(separator.asCompose())
                append(str.asCompose())
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