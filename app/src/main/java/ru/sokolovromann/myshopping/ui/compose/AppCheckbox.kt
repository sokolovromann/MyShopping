package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.CheckboxData
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppCheckbox(
    modifier: Modifier = Modifier,
    data: CheckboxData,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = data.checkedColor.asCompose(),
        uncheckedColor = data.uncheckedColor.asCompose(),
        checkmarkColor = data.checkmarkColor.asCompose()
    )

    Checkbox(
        modifier = modifier,
        checked = data.checked,
        onCheckedChange = onCheckedChange,
        colors = colors
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppCheckboxPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppCheckbox(
                    modifier = Modifier.padding(8.dp),
                    data = CheckboxData(checked = false)
                )
                AppCheckbox(
                    modifier = Modifier.padding(8.dp),
                    data = CheckboxData(checked = true)
                )
            }
        }
    }
}