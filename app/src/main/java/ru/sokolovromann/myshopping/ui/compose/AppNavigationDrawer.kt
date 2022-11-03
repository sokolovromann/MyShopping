package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.NavigationDrawerData
import ru.sokolovromann.myshopping.ui.compose.state.RouteItemData
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.theme.AppColor
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

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
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = data
                    .backgroundColor()
                    .asCompose()
            )
            .padding(start = 16.dp),
        onClick = onClick,
        before = { AppIcon(data = data.icon()) },
        title = {
            AppText(
                modifier = Modifier.padding(horizontal = 16.dp),
                data = data.name()
            )
        }
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppNavigationDrawerPreview() {
    MyShoppingTheme {
        AppNavigationDrawer(
            data = NavigationDrawerData(items = NavigationDrawerData.defaultItems(checked = UiRoute.Trash)),
            onClick = {}
        )
    }
}