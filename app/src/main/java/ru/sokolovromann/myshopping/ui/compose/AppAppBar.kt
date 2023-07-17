package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

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
        navigationIcon = getAppNavigationIconOrNull(contentColor, navigationIcon),
        actions = {
            AppAppBarComposition(contentColor) { actions() }
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Composable
fun AppBottomAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppBottomAppBarMinHeight),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column {
            Divider()
            AppBottomBarImpl(
                modifier = modifier,
                actionButtons = actionButtons,
                contentColor = contentColor,
                content = content
            )
        }
    }
}

@Composable
private fun getAppNavigationIconOrNull(
    contentColor: Color,
    navigationIcon: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    return if (navigationIcon == null) {
        null
    } else {
        { AppAppBarComposition(contentColor) { navigationIcon() } }
    }
}

@Composable
private fun AppBottomBarImpl(
    modifier: Modifier,
    actionButtons: @Composable (RowScope.() -> Unit)?,
    contentColor: Color,
    content: @Composable (RowScope.() -> Unit)?
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content?.let {
            val contentModifier = if (actionButtons == null) {
                Modifier
            } else {
                Modifier.weight(0.99f)
            }
            Row(modifier = contentModifier) {
                AppAppBarComposition(
                    contentColor = contentColor,
                    textColor = MaterialTheme.colors.primary,
                    content = { it() }
                )
            }
        }
        actionButtons?.let {
            val actionModifier = if (content == null) {
                Modifier
            } else {
                Modifier.weight(0.01f)
            }
            Spacer(modifier = actionModifier)
            AppAppBarComposition(contentColor) { it() }
        }
    }
}

@Composable
private fun AppAppBarComposition(
    contentColor: Color,
    contentAlpha: Float = ContentAlpha.medium,
    textColor: Color = contentColor,
    content: @Composable () -> Unit
) {
    val textStyle = TextStyle.Default.copy(color = textColor)

    CompositionLocalProvider(
        values = arrayOf(
            LocalContentColor provides contentColor,
            LocalContentAlpha provides contentAlpha,
            LocalTextStyle provides textStyle
        ),
        content = content
    )
}

private val AppBottomAppBarMinHeight = 56.dp