package ru.sokolovromann.myshopping.ui.compose.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

sealed class UiIcon {

    object Nothing : UiIcon()

    data class FromVector(val imageVector: ImageVector) : UiIcon()

    data class FromResources(@DrawableRes val id: Int) : UiIcon()

    @Composable
    fun asPainter(): Painter? = when (this) {
        Nothing -> null
        is FromVector -> rememberVectorPainter(imageVector)
        is FromResources -> painterResource(id)
    }
}