package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class RouteItemData(
    val name: TextData = TextData(),
    val icon: IconData = IconData(),
    val checked: Boolean = false,
    private val checkedBackgroundColor: ColorData = ColorData(
        appColor = AppColor.Surface
    ),
    private val uncheckedBackgroundColor: ColorData = ColorData(
        appColor = AppColor.Transparent
    )
) {

    fun backgroundColor(): ColorData = if (checked) {
        checkedBackgroundColor
    } else {
        uncheckedBackgroundColor
    }
}