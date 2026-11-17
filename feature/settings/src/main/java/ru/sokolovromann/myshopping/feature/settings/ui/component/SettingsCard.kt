package ru.sokolovromann.myshopping.feature.settings.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.component.GridItem

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
        content()
    }
}

@Composable
fun SettingsCardItem(
    title: @Composable () -> Unit,
    body: @Composable () -> Unit,
    onClick: () -> Unit
) {
    GridItem(
        title = title,
        body = body,
        onClick = onClick,
        borderEnabled = false
    )
}