package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorSupportingText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error
    )
}