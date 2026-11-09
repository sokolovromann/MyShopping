package ru.sokolovromann.myshopping.core.ui.utils

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun getWindowSize() = currentWindowAdaptiveInfo().windowSizeClass