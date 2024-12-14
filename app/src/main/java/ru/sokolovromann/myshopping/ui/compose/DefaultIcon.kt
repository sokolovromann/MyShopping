package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString

@Composable
fun DefaultIcon(
    icon: UiIcon,
    modifier: Modifier = Modifier,
    contentDescription: UiString? = null,
    tint: Color = DefaultIconTint
) {
    Icon(
        painter = icon.asPainter(),
        contentDescription = contentDescription?.asCompose(),
        modifier = modifier,
        tint = tint
    )
}

private val DefaultIconTint: Color
    @Composable
    get() = LocalContentColor.current.copy(
        alpha = ContentAlpha.medium
    )