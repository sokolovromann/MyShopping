package ru.sokolovromann.myshopping.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingTheme

@Composable
fun DefaultProgressIndicator(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Column(
        modifier = modifier
            .background(Color.Transparent)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        text?.let {
            Text(
                modifier = Modifier.padding(8.dp),
                text = it
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultProgressIndicatorPreview() {
    MyShoppingTheme {
        DefaultProgressIndicator(text = "Please, wait...")
    }
}