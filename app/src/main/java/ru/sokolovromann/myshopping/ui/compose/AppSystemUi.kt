package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.SystemUiController
import ru.sokolovromann.myshopping.ui.compose.state.SystemUiData

@Composable
fun AppSystemUi(
    systemUiController: SystemUiController,
    data: SystemUiData
) {
    val statusBarColor = data.statusBarColor.asCompose()
    val statusBarDarkIcons = data.statusBarIconsColor.asCompose().luminance() > 0.5f
    val navigationBarColor = data.navigationBarColor.asCompose()
    val navigationBarDarkIcons = data.navigationBarIconsColor.asCompose().luminance() > 0.5f

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