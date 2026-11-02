package ru.sokolovromann.myshopping.core.ui.utils

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun getWindowSize() = currentWindowAdaptiveInfo().windowSizeClass

fun String.toTextFieldValue() = TextFieldValue(
    text = this,
    selection = TextRange(this.length),
    composition = TextRange(this.length)
)