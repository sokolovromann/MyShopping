package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    Checkbox(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors
    )
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