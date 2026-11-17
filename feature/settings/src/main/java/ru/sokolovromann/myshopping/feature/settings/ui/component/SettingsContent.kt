package ru.sokolovromann.myshopping.feature.settings.ui.component

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.feature.settings.ui.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.model.SettingsState

@Composable
fun SettingsContent(
    onGeneralSettingsAction: (GeneralSettingsAction) -> Unit,
    onCartsSettingsAction: (CartsSettingsAction) -> Unit,
    onProductsSettingsAction: (ProductsSettingsAction) -> Unit,
    onProductsWidgetSettingsAction: (ProductsWidgetSettingsAction) -> Unit,
    onAddEditProductSettingsAction: (AddEditProductSettingsAction) -> Unit,
    onSuggestionsSettingsAction: (SuggestionsSettingsAction) -> Unit,
    onBackupSettingsAction: (BackupSettingsAction) -> Unit,
    settingsState: SettingsState
) {
    if (settingsState.isProgress) {
        LoadingContent()
    } else {
        SimpleVerticalGrid {
            item {
                SettingsGeneralCard(
                    onAction = onGeneralSettingsAction,
                    preferences = settingsState.generalPreferences
                )
            }
            item {
                SettingsCartsCard(
                    onAction = onCartsSettingsAction,
                    preferences = settingsState.cartsPreferences
                )
            }
            item {
                SettingsProductsCard(
                    onAction = onProductsSettingsAction,
                    preferences = settingsState.productPreferences
                )
            }
            item {
                SettingsAddEditProductCard(
                    onAction = onAddEditProductSettingsAction,
                    preferences = settingsState.addEditProductPreferences
                )
            }
            item {
                SettingsSuggestionsCard(
                    onAction = onSuggestionsSettingsAction,
                    preferences = settingsState.suggestionsPreferences
                )
            }
            item {
                SettingsProductsWidgetCard(
                    onAction = onProductsWidgetSettingsAction,
                    preferences = settingsState.productsWidgetPreferences
                )
            }
            item {
                SettingsBackupCard(
                    onAction = onBackupSettingsAction,
                    preferences = settingsState.backupPreferences
                )
            }
            item { BottomGridSpacer() }
        }
    }
}