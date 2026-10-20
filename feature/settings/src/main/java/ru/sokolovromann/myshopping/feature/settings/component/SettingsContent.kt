package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.feature.settings.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

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
    if (settingsState.isLoading) {
        LoadingContent()
    } else {
        SimpleVerticalGrid {
            item {
                GeneralSettingsCard(
                    onAction = onGeneralSettingsAction,
                    items = settingsState.generalItems
                )
            }
            item {
                CartsSettingsCard(
                    onAction = onCartsSettingsAction,
                    items = settingsState.cartsItems
                )
            }
            item {
                ProductsSettingsCard(
                    onAction = onProductsSettingsAction,
                    items = settingsState.productsItems
                )
            }
            item {
                ProductsWidgetSettingsCard(
                    onAction = onProductsWidgetSettingsAction,
                    items = settingsState.productsWidgetItems
                )
            }
            item {
                AddEditProductSettingsCard(
                    onAction = onAddEditProductSettingsAction,
                    items = settingsState.addEditProductItems
                )
            }
            item {
                SuggestionsSettingsCard(
                    onAction = onSuggestionsSettingsAction,
                    items = settingsState.suggestionsItems
                )
            }
            item {
                BackupSettingsCard(
                    onAction = onBackupSettingsAction,
                    items = settingsState.backupItems
                )
            }
            item { BottomGridSpacer() }
        }
    }
}