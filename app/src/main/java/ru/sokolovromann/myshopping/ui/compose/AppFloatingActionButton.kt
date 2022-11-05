package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.FloatingActionButtonData
import ru.sokolovromann.myshopping.ui.compose.state.IconData
import ru.sokolovromann.myshopping.ui.compose.state.UiIcon
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppFloatingActionButton(
    modifier: Modifier = Modifier,
    data: FloatingActionButtonData,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        backgroundColor = data.backgroundColor.asCompose(),
        contentColor = data.contentColor.asCompose(),
        content = { AppIcon(data = data.icon) }
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppFloatingActionButtonPreview() {
    MyShoppingTheme {
        Column {
            AppFloatingActionButton(
                modifier = Modifier.padding(8.dp),
                data = FloatingActionButtonData(
                    icon = IconData(icon = UiIcon.FromVector(Icons.Default.Add))
                ),
                onClick = {}
            )
        }
    }
}