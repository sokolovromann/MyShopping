package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiProductsWidgetPreferences(
    val theme: UiPreference<Theme> = UiPreference(UserPreferencesDefaults.ProductsWidget.theme),
    val fontSize: UiPreference<FontSize> = UiPreference(UserPreferencesDefaults.ProductsWidget.fontSize),
    val groupProducts: UiPreference<GroupProductsByStatus> = UiPreference(UserPreferencesDefaults.ProductsWidget.groupProductsByStatus)
)