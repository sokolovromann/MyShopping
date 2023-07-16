package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AppTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = getAppTopAppBarNavigationIconOrNull(contentColor, navigationIcon),
        actions = getAppTopAppBarActions(contentColor, actions),
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Composable
private fun getAppTopAppBarNavigationIconOrNull(
    contentColor: Color,
    navigationIcon: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    return if (navigationIcon == null) {
        null
    } else {
        { AppTopAppBarComposition(contentColor, navigationIcon) }
    }
}

@Composable
private fun getAppTopAppBarActions(
    contentColor: Color,
    actions: @Composable RowScope.() -> Unit
): @Composable RowScope.() -> Unit = {
    AppTopAppBarComposition(color = contentColor) { actions() }
}

@Composable
private fun AppTopAppBarComposition(
    color: Color,
    content: @Composable () -> Unit
) {
    val textStyle = TextStyle.Default.copy(color = color)

    CompositionLocalProvider(
        values = arrayOf(
            LocalContentColor provides color,
            LocalContentAlpha provides ContentAlpha.medium,
            LocalTextStyle provides textStyle
        ),
        content = content
    )
}