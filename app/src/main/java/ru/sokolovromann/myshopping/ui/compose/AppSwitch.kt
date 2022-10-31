package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.SwitchData
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    data: SwitchData,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val colors = SwitchDefaults.colors(
        checkedThumbColor = data.checkedThumbColor.asCompose(),
        checkedTrackColor = data.checkedTrackColor.asCompose(),
        uncheckedThumbColor = data.uncheckedThumbColor.asCompose(),
        uncheckedTrackColor = data.uncheckedTrackColor.asCompose()
    )

    Switch(
        modifier = modifier,
        checked = data.checked,
        onCheckedChange = onCheckedChange,
        colors = colors
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppSwitchPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppSwitch(
                    modifier = Modifier.padding(8.dp),
                    data = SwitchData(checked = false)
                )
                AppSwitch(
                    modifier = Modifier.padding(8.dp),
                    data = SwitchData(checked = true)
                )
            }
        }
    }
}