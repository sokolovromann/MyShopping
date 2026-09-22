package ru.sokolovromann.myshopping.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SimpleScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable (() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    onBackClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    BackHandler { onBackClick() }
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = { paddings ->
            Box(
                modifier = Modifier.padding(paddings),
                contentAlignment = Alignment.TopStart,
                content = content
            )
        }
    )
}