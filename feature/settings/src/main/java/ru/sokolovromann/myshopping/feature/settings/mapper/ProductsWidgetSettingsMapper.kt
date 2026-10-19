package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.FontSize
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.Theme
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.ProductsWidgetSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

class ProductsWidgetSettingsMapper @Inject constructor() {

    fun toItems(preferences: ProductsWidgetPreferences): ProductsWidgetSettingsItems {
        val theme = SettingsItem(
            selected = preferences.theme,
            text = when (preferences.theme) {
                Theme.Default -> UiText.FromResources(R.string.settings_text_theme_default)
                Theme.Light -> UiText.FromResources(R.string.settings_text_theme_light)
                Theme.Dark -> UiText.FromResources(R.string.settings_text_theme_dark)
            }
        )
        val fontSize = SettingsItem(
            selected = preferences.fontSize,
            text = when (preferences.fontSize) {
                FontSize.Small -> UiText.FromResources(R.string.settings_text_font_size_small)
                FontSize.Medium -> UiText.FromResources(R.string.settings_text_font_size_medium)
                FontSize.Large -> UiText.FromResources(R.string.settings_text_font_size_large)
                FontSize.ExtraLarge -> UiText.FromResources(R.string.settings_text_font_size_extra_large)
                FontSize.Huge -> UiText.FromResources(R.string.settings_text_font_size_huge)
                FontSize.ExtraHuge -> UiText.FromResources(R.string.settings_text_font_size_extra_huge)
            }
        )
        val groupProducts = SettingsItem(
            selected = preferences.groupProductsByStatus,
            text = when (preferences.groupProductsByStatus) {
                GroupProductsByStatus.CompletedFirst ->
                    UiText.FromResources(R.string.settings_text_group_products_by_status_completed_first)
                GroupProductsByStatus.ActiveFirst ->
                    UiText.FromResources(R.string.settings_text_group_products_by_status_active_first)
                GroupProductsByStatus.HideCompleted ->
                    UiText.FromResources(R.string.settings_text_group_products_by_status_hide_completed)
                GroupProductsByStatus.DoNotGroup ->
                    UiText.FromResources(R.string.settings_text_group_products_by_status_do_not_group)
            }
        )
        return ProductsWidgetSettingsItems(
            theme = theme,
            fontSize = fontSize,
            groupProducts = groupProducts
        )
    }
}