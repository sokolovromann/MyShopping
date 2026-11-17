package ru.sokolovromann.myshopping.feature.settings.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.ui.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiProductsWidgetPreferences

@Composable
fun SettingsProductsWidgetCard(
    onAction: (ProductsWidgetSettingsAction) -> Unit,
    preferences: UiProductsWidgetPreferences
) {
    SettingsCard(stringResource(R.string.settings_text_products_widget)) {
        ThemeItem(preferences.theme) {
            val action = ProductsWidgetSettingsAction.OnThemeSelected(it)
            onAction(action)
        }
        FontSizeItem(preferences.fontSize) {
            val action = ProductsWidgetSettingsAction.OnFontSizeSelected(it)
            onAction(action)
        }
        GroupProductsItem(preferences.groupProducts) {
            val action = ProductsWidgetSettingsAction.OnGroupProductsSelected(it)
            onAction(action)
        }
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(R.string.settings_text_widget_more_settings),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}