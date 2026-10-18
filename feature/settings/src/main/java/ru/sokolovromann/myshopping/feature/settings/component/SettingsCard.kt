package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.component.GridItem

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            content()
        }
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