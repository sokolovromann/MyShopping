package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class ProductsWidgetSettingsItems(
    val theme: SettingsItem<Theme> = SettingsItem(UserPreferencesDefaults.ProductsWidget.theme),
    val fontSize: SettingsItem<FontSize> = SettingsItem(UserPreferencesDefaults.ProductsWidget.fontSize),
    val groupProducts: SettingsItem<GroupProductsByStatus> = SettingsItem(UserPreferencesDefaults.ProductsWidget.groupProductsByStatus)
)