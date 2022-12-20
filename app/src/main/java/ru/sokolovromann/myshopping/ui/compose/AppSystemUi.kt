package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.SystemUiController

@Composable
fun AppSystemUi(
    systemUiController: SystemUiController,
    statusBarColor: Color = MaterialTheme.colors.primarySurface,
    navigationBarColor: Color = MaterialTheme.colors.background,
) {
    val statusBarDarkIcons = contentColorFor(statusBarColor).luminance() < 0.5f
    val navigationBarDarkIcons = contentColorFor(navigationBarColor).luminance() < 0.5f

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = statusBarDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = navigationBarDarkIcons
        )
    }
}