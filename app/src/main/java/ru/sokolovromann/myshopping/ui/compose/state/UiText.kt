package ru.sokolovromann.myshopping.ui.compose.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {

    object Nothing : UiText()

    data class FromLong(val value: Long) : UiText()

    data class FromInt(val value: Int) : UiText()

    data class FromFloat(val value: Float) : UiText()

    data class FromString(val value: String) : UiText()

    data class FromResources(@StringRes val id: Int) : UiText()

    class FromResourcesWithArgs(@StringRes val id: Int, vararg val args: Any) : UiText()

    @Composable
    fun asCompose(): String = when (this) {
        Nothing -> ""
        is FromLong -> value.toString()
        is FromInt -> value.toString()
        is FromFloat -> value.toString()
        is FromString -> value
        is FromResources -> stringResource(id)
        is FromResourcesWithArgs -> stringResource(id, args)
    }
}