package ru.sokolovromann.myshopping.core.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

@Immutable
sealed class UiIcon {

    data class FromVector(val imageVector: ImageVector) : UiIcon()

    data class FromResources(val id: Int) : UiIcon()

    @Composable
    fun asPainter(): Painter = when (this) {
        is FromVector -> rememberVectorPainter(imageVector)
        is FromResources -> painterResource(id)
    }

    override fun toString(): String = ""
}