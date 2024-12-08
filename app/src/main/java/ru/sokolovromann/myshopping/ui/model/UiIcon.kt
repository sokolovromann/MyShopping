package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import ru.sokolovromann.myshopping.R

sealed class UiIcon {

    data class FromVector(val imageVector: ImageVector) : UiIcon()

    data class FromResources(@DrawableRes val id: Int) : UiIcon()

    companion object {
        val Archive: UiIcon = FromResources(R.drawable.ic_all_archive)
        val Back: UiIcon = FromVector(Icons.Default.ArrowBack)
        val Cancel: UiIcon = FromVector(Icons.Default.Clear)
        val Clear: UiIcon = FromVector(Icons.Default.Clear)
        val Close: UiIcon = FromVector(Icons.Default.Close)
        val Delete: UiIcon = FromVector(Icons.Default.Delete)
        val DisplayHidden: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val DisplayOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val HideOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowUp)
        val NavigationMenu: UiIcon = FromVector(Icons.Default.Menu)
        val Reminder: UiIcon = FromResources(R.drawable.ic_all_reminder)
        val Restore: UiIcon = FromResources(R.drawable.ic_all_restore)
        val SelectAll: UiIcon = FromResources(R.drawable.ic_all_select_all)
        val Unarchive: UiIcon = FromResources(R.drawable.ic_all_unarchive)
    }

    @Composable
    fun asPainter(): Painter = when (this) {
        is FromVector -> rememberVectorPainter(imageVector)
        is FromResources -> painterResource(id)
    }
}