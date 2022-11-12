package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sokolovromann.myshopping.ui.compose.state.*

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    data: TopBarData,
    onNavigationIconClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = { AppText(data = data.title) },
        navigationIcon = navigationIcon(
            data = data.navigationIcon,
            onClick = onNavigationIconClick
        ),
        backgroundColor = data.backgroundColor.asCompose(),
        contentColor = data.contentColor.asCompose(),
        actions = actions
    )
}

@Composable
private fun navigationIcon(
    data: IconData,
    onClick: () -> Unit
): @Composable (() -> Unit)? {
    return if (data.icon == UiIcon.Nothing) {
        null
    } else {
        {
            IconButton(
                onClick = onClick,
                content = { AppIcon(data = data) }
            )
        }
    }
}