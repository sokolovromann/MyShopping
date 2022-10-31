package ru.sokolovromann.myshopping.ui.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.ui.compose.state.RadioButtonData
import ru.sokolovromann.myshopping.ui.theme.MyShoppingTheme

@Composable
fun AppRadioButton(
    modifier: Modifier = Modifier,
    data: RadioButtonData,
    onClick: (() -> Unit)? = null
) {
    val colors = RadioButtonDefaults.colors(
        selectedColor = data.selectedColor.asCompose(),
        unselectedColor = data.unselectedColor.asCompose()
    )

    RadioButton(
        modifier = modifier,
        selected = data.selected,
        onClick = onClick,
        colors = colors
    )
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AppRadioButtonPreview() {
    MyShoppingTheme {
        Surface {
            Column {
                AppRadioButton(
                    modifier = Modifier.padding(8.dp),
                    data = RadioButtonData.OnSurface.copy(selected = false)
                )

                AppRadioButton(
                    modifier = Modifier.padding(8.dp),
                    data = RadioButtonData.OnSurface.copy(selected = true)
                )
            }
        }
    }
}