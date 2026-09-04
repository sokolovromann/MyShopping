package ru.sokolovromann.myshopping

import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingThemeFontSize
import ru.sokolovromann.myshopping.core.ui.theme.MyShoppingThemeType

data class MainState(
    val isWaiting: Boolean = true,
    val themeType: MyShoppingThemeType = MyShoppingThemeType.Dynamic,
    val themeFontSize: MyShoppingThemeFontSize = MyShoppingThemeFontSize.Medium
)