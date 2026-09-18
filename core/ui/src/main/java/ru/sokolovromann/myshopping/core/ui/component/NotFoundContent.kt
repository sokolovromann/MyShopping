package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.model.UiIcon
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Composable
fun NotFoundContent(
    text: UiText,
    icon: UiIcon? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon?.let {
            Icon(
                painter = it.asPainter(),
                contentDescription = text.asCompose()
            )
        }
        Text(
            text = text.asCompose(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}