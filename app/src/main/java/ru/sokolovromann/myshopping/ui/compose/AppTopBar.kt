package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.*

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    data: TopBarData,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = { AppText(data = data.title) },
        navigationIcon = navigationIcon(data = data.navigationIcon),
        backgroundColor = data.backgroundColor.asCompose(),
        contentColor = data.contentColor.asCompose(),
        actions = actions
    )
}

@Composable
private fun navigationIcon(data: IconData): @Composable (() -> Unit)? {
    return if (data.icon == UiIcon.Nothing) {
        null
    } else {
        {
            AppIcon(
                modifier = Modifier.padding(start = 12.dp, end = 20.dp),
                data = data
            )
        }
    }
}