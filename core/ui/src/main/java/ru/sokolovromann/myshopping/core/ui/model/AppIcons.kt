package ru.sokolovromann.myshopping.core.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import ru.sokolovromann.myshopping.core.ui.R
import ru.sokolovromann.myshopping.core.ui.model.UiIcon.FromResources
import ru.sokolovromann.myshopping.core.ui.model.UiIcon.FromVector

object AppIcons {

    val About: UiIcon = FromVector(Icons.Default.Info)

    val Dictionary: UiIcon = FromVector(Icons.AutoMirrored.Default.List)

    val Purchases: UiIcon = FromResources(R.drawable.ic_purchases)

    val Trash: UiIcon = FromVector(Icons.Default.Delete)
}