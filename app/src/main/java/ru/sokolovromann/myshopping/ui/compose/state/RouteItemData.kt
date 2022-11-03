package ru.sokolovromann.myshopping.ui.compose.state

import ru.sokolovromann.myshopping.ui.theme.AppColor

data class RouteItemData(
    private val uiText: UiText,
    private val uiIcon: UiIcon,
    private val checked: Boolean = false,
    private val checkedBackgroundColor: ColorData = ColorData(
        appColor = AppColor.Surface
    ),
    private val checkedTextColor: ColorData = ColorData(
        appColor = AppColor.Secondary
    ),
    private val checkedIconColor: ColorData = ColorData(
        appColor = AppColor.Secondary
    ),
    private val uncheckedBackgroundColor: ColorData = ColorData(
        appColor = AppColor.Transparent
    ),
    private val uncheckedTextColor: ColorData = ColorData(
        appColor = AppColor.OnSurface
    ),
    private val uncheckedIconColor: ColorData = ColorData(
        appColor = AppColor.OnSurface,
        alpha = 0.7f
    )
) {

    fun name(): TextData = TextData.Body.copy(
        text = uiText,
        color = if (checked) checkedTextColor else uncheckedTextColor
    )

    fun icon(): IconData = IconData(
        icon = uiIcon,
        tint = if (checked) checkedIconColor else uncheckedIconColor
    )

    fun backgroundColor(): ColorData = if (checked) {
        checkedBackgroundColor
    } else {
        uncheckedBackgroundColor
    }
}