package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicator(
    text: String? = null,
    color: Color = ProgressIndicatorDefaults.circularColor,
) {
    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = color)
        text?.let {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                text = it
            )
        }
    }
}