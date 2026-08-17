package ru.sokolovromann.myshopping.core.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import ru.sokolovromann.myshopping.core.ui.R
import ru.sokolovromann.myshopping.core.ui.extension.toUiIcon

object AppIcons {

    val About: UiIcon = Icons.Default.Info.toUiIcon()

    val Dictionary: UiIcon = Icons.AutoMirrored.Default.List.toUiIcon()

    val Purchases: UiIcon = R.drawable.ic_purchases.toUiIcon()

    val Trash: UiIcon = Icons.Default.Delete.toUiIcon()
}