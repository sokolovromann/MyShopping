package ru.sokolovromann.myshopping.core.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {

    data class FromString(val text: String) : UiText()

    data class FromResources(val id: Int): UiText()

    class FromResourcesWithArgs(val id: Int, vararg val args: Any) : UiText()

    @Composable
    fun asCompose(): String = when (this) {
        is FromString -> text
        is FromResources -> stringResource(id)
        is FromResourcesWithArgs -> stringResource(id, args)
    }

    @Composable
    fun isEmpty(): Boolean = asCompose().isEmpty()

    @Composable
    fun isNotEmpty(): Boolean = asCompose().isNotEmpty()

    override fun toString(): String = ""
}