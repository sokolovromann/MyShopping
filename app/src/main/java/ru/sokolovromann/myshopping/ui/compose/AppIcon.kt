package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.IconData
import ru.sokolovromann.myshopping.ui.compose.state.UiIcon
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppIcon(modifier: Modifier = Modifier, data: IconData) {
    val icon = data.icon.asPainter() ?: return

    Icon(
        modifier = modifier.size(data.size),
        painter = icon,
        contentDescription = data.contentDescription.asCompose(),
        tint = data.tint.asCompose()
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppIconPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnSurface.copy(icon = UiIcon.FromVector(Icons.Default.Settings), size = 36.dp)
                )

                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnSurface.copy(icon = UiIcon.FromVector(Icons.Default.Add), size = 48.dp)
                )

                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnSurface.copy(icon = UiIcon.FromVector(Icons.Default.Delete))
                )

                AppIcon(
                    modifier = Modifier.padding(8.dp),
                    data = IconData.OnSurface.copy(icon = UiIcon.Nothing)
                )
            }
        }
    }
}