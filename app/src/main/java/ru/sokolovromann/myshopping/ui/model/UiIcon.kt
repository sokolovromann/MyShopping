package ru.sokolovromann.myshopping.ui.model

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
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
        val About: UiIcon = FromVector(Icons.Default.Info)
        val Add: UiIcon = FromVector(Icons.Default.Add)
        val Autocompletes: UiIcon = FromVector(Icons.Default.List)
        val Archive: UiIcon = FromResources(R.drawable.ic_all_archive)
        val Back: UiIcon = FromVector(Icons.Default.ArrowBack)
        val Cancel: UiIcon = FromVector(Icons.Default.Clear)
        val Checkbox: UiIcon = FromResources(R.drawable.ic_all_check_box)
        val CheckboxOutline: UiIcon = FromResources(R.drawable.ic_all_check_box_outline)
        val Clear: UiIcon = FromVector(Icons.Default.Clear)
        val ClearAutocompletes: UiIcon = FromResources(R.drawable.ic_autocompletes_clear)
        val ClearProducts: UiIcon = FromResources(R.drawable.ic_autocompletes_clear)
        val Close: UiIcon = FromVector(Icons.Default.Close)
        val Delete: UiIcon = FromVector(Icons.Default.Delete)
        val DisplayHidden: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val DisplayOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val Edit: UiIcon = FromVector(Icons.Default.Edit)
        val HideOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowUp)
        val More: UiIcon = FromVector(Icons.Default.MoreVert)
        val MoreMenu: UiIcon = FromVector(Icons.Default.KeyboardArrowRight)
        val MoveDown: UiIcon = FromResources(R.drawable.ic_all_arrow_down)
        val MoveUp: UiIcon = FromResources(R.drawable.ic_all_arrow_up)
        val NavigationMenu: UiIcon = FromVector(Icons.Default.Menu)
        val Pin: UiIcon = FromResources(R.drawable.ic_all_pin)
        val Purchases: UiIcon = FromResources(R.drawable.ic_all_purchases)
        val Reminder: UiIcon = FromResources(R.drawable.ic_all_reminder)
        val Rename: UiIcon = FromResources(R.drawable.ic_all_rename)
        val Restore: UiIcon = FromResources(R.drawable.ic_all_restore)
        val SelectAll: UiIcon = FromResources(R.drawable.ic_all_select_all)
        val SelectFromAutocompletes: UiIcon = FromResources(R.drawable.ic_select_from_autocompletes)
        val Settings: UiIcon = FromVector(Icons.Default.Settings)
        val Unarchive: UiIcon = FromResources(R.drawable.ic_all_unarchive)
        val Unpin: UiIcon = FromResources(R.drawable.ic_all_unpin)
    }

    @Composable
    fun asPainter(): Painter = when (this) {
        is FromVector -> rememberVectorPainter(imageVector)
        is FromResources -> painterResource(id)
    }
}