package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    data: BottomBarData,
    content: @Composable RowScope.() -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        backgroundColor = data.backgroundColor.asCompose(),
        contentColor = data.contentColor.asCompose(),
        content = content
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppBottomBarPreview() {
    MyShoppingTheme {
        Column {
            AppBottomBar(
                modifier = Modifier.padding(8.dp),
                data = BottomBarData(),
                content = {}
            )

            AppBottomBar(
                modifier = Modifier.padding(8.dp),
                data = BottomBarData()
            ) {
                AppText(
                    modifier = Modifier.padding(8.dp),
                    data = TextData.BottomAppBar.copy(text = UiText.FromString("My Shopping"))
                )
                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnBottomAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Favorite))
                )
                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnBottomAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Share))
                )
                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnBottomAppBar.copy(icon = UiIcon.FromVector(Icons.Default.Send))
                )
            }
        }
    }
}