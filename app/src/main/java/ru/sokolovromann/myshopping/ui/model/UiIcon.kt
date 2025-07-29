package ru.sokolovromann.myshopping.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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

    data class FromResources(val id: Int) : UiIcon()

    companion object {
        val About: UiIcon = FromVector(Icons.Default.Info)
        val Add: UiIcon = FromVector(Icons.Default.Add)
        val Autocompletes: UiIcon = FromVector(Icons.AutoMirrored.Filled.List)
        val Archive: UiIcon = FromResources(R.drawable.ic_all_archive)
        val ArchiveUnarchive: UiIcon = FromResources(R.drawable.ic_all_archive_unarchive)
        val Back: UiIcon = FromVector(Icons.AutoMirrored.Filled.ArrowBack)
        val Cancel: UiIcon = FromVector(Icons.Default.Clear)
        val Checkbox: UiIcon = FromResources(R.drawable.ic_all_check_box)
        val CheckboxOutline: UiIcon = FromResources(R.drawable.ic_all_check_box_outline)
        val Clear: UiIcon = FromVector(Icons.Default.Clear)
        val ClearAutocompletes: UiIcon = FromResources(R.drawable.ic_autocompletes_clear)
        val ClearProducts: UiIcon = FromResources(R.drawable.ic_autocompletes_clear)
        val Close: UiIcon = FromVector(Icons.Default.Close)
        val CompletedActive: UiIcon = FromResources(R.drawable.ic_all_completed_active)
        val Delete: UiIcon = FromVector(Icons.Default.Delete)
        val DisplayHidden: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val DisplayOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowDown)
        val Duplicate: UiIcon = FromResources(R.drawable.ic_all_duplicate)
        val Edit: UiIcon = FromVector(Icons.Default.Edit)
        val HideOtherFields: UiIcon = FromVector(Icons.Default.KeyboardArrowUp)
        val More: UiIcon = FromVector(Icons.Default.MoreVert)
        val MoreMenu: UiIcon = FromVector(Icons.AutoMirrored.Filled.KeyboardArrowRight)
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