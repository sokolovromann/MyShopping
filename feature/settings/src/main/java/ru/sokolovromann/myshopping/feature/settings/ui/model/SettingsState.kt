package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val isProgress: Boolean = true,
    val generalPreferences: UiGeneralPreferences = UiGeneralPreferences(),
    val cartsPreferences: UiCartsPreferences = UiCartsPreferences(),
    val productPreferences: UiProductsPreferences = UiProductsPreferences(),
    val productsWidgetPreferences: UiProductsWidgetPreferences = UiProductsWidgetPreferences(),
    val addEditProductPreferences: UiAddEditProductPreferences = UiAddEditProductPreferences(),
    val suggestionsPreferences: UiSuggestionsPreferences = UiSuggestionsPreferences(),
    val backupPreferences: UiBackupPreferences = UiBackupPreferences()
)