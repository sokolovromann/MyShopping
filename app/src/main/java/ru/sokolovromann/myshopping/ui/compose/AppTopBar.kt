package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

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

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppTopBarPreview() {
    MyShoppingTheme {
        Column {
            AppTopBar(
                modifier = Modifier.padding(8.dp),
                data = TopBarData.Default(UiText.FromString("My Shopping List"))
            )

            AppTopBar(
                modifier = Modifier.padding(8.dp),
                data = TopBarData(
                    title = TextData.TopAppBar.copy(text = UiText.FromString("My Shopping List")),
                    navigationIcon = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Menu))
                ),
                actions = {
                    AppIcon(data = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Send)))
                }
            )

            AppTopBar(
                modifier = Modifier.padding(8.dp),
                data = TopBarData(
                    navigationIcon = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.ArrowBack))
                ),
                actions = {
                    AppIcon(
                        modifier = Modifier.padding(8.dp),
                        data = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Favorite))
                    )
                    AppIcon(
                        modifier = Modifier.padding(8.dp),
                        data = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Share))
                    )
                    AppIcon(
                        modifier = Modifier.padding(8.dp),
                        data = IconData.OnTopAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Send))
                    )
                }
            )
        }
    }
}