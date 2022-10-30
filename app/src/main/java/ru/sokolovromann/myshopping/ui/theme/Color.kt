package ru.sokolovromann.myshopping.ui.theme

import androidx.compose.ui.graphics.Color

sealed class AppColor(val light: Color, val dark: Color) {
    object Primary : AppColor(Green900, Green300)
    object PrimaryVariant : AppColor(Green900, Green300)
    object Secondary : AppColor(Green900, Green300)
    object SecondaryVariant : AppColor(Green900, Green300)
    object Surface : AppColor(Color.White, Gray900)
    object Background : AppColor(Gray200, Color.Black)
    object Error : AppColor(Color.Red, Color.Red)
    object OnPrimary : AppColor(Color.White, Color.Black)
    object OnSecondary : AppColor(Color.White, Color.Black)
    object OnSurface : AppColor(Color.Black, Color.White)
    object OnBackground : AppColor(Color.Black, Color.White)
    object OnError : AppColor(Color.Black, Color.Black)
}

private val Green300 = Color(0XFF81C784)
private val Green900 = Color(0XFF1B5E20)
private val Gray200 = Color(0xFFEEEEEE)
private val Gray900 = Color(0xFF212121)