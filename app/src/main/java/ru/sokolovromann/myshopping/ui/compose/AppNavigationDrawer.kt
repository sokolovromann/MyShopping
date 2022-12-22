package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.NavigationDrawerData
import ru.sokolovromann.myshopping.ui.compose.state.RouteItemData
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.theme.AppColor

@Composable
fun AppNavigationDrawer(
    data: NavigationDrawerData,
    onClick: (RouteItemData) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(data.backgroundColor.asCompose())
    ) {
        AppNavigationHeader(header = data.header)

        Divider(
            color = AppColor.Transparent.asCompose(),
            thickness = 8.dp
        )

        AppNavigationItems(
            data = data,
            onClick = onClick
        )
    }
}

@Composable
private fun AppNavigationHeader(header: TextData) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp)
    ) {
        AppText(data = header)
    }
}

@Composable
private fun AppNavigationItems(
    data: NavigationDrawerData,
    onClick: (RouteItemData) -> Unit
) {
    data.items.forEach { item ->
        AppNavigationItem(
            data = item,
            onClick = { onClick(item) }
        )
    }
}


@Composable
private fun AppNavigationItem(
    data: RouteItemData,
    onClick: () -> Unit
) {
    AppItem(
        onClick = onClick,
        before = { AppIcon(data = data.icon) },
        title = { Text(text = data.name.text.asCompose()) },
        backgroundColor = data.backgroundColor().asCompose()
    )
}