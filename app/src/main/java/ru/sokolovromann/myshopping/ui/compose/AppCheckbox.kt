package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
        Checkbox(
            modifier = modifier,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = colors
        )
    }
}

@Composable
fun CheckmarkAppCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    backgroundColor: Color = Color.Transparent,
    checkmarkColor: Color = MaterialTheme.colors.secondary
) {
    val colors = CheckboxDefaults.colors(
        checkedColor = backgroundColor,
        uncheckedColor = backgroundColor,
        checkmarkColor = checkmarkColor,
        disabledColor = backgroundColor,
        disabledIndeterminateColor = backgroundColor
    )

    Checkbox(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors
    )
}