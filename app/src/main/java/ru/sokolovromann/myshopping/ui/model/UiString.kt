package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.ui.compose.state.UiText

sealed class UiString {

    data class FromString(val value: String) : UiString()

    data class FromResources(@StringRes val id: Int) : UiString()

    class FromResourcesWithArgs(@StringRes val id: Int, vararg val args: Any) : UiString()

    @Composable
    fun asCompose(): String = when (this) {
        is FromString -> value
        is FromResources -> stringResource(id)
        is FromResourcesWithArgs -> stringResource(id, *args)
    }

    @Composable
    fun isEmpty(): Boolean {
        return this.asCompose().isEmpty()
    }

    @Composable
    fun isNotEmpty(): Boolean {
        return this.asCompose().isNotEmpty()
    }

    fun toUiText(): UiText = when (this) {
        is FromString -> UiText.FromString(value)
        is FromResources -> UiText.FromResources(id)
        is FromResourcesWithArgs -> UiText.FromResourcesWithArgs(id, args)
    }
}