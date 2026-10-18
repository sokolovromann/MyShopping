package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.feature.settings.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

@Composable
fun SettingsContent(
    onGeneralSettingsAction: (GeneralSettingsAction) -> Unit,
    onCartsSettingsAction: (CartsSettingsAction) -> Unit,
    onProductsSettingsAction: (ProductsSettingsAction) -> Unit,
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
            item { BottomGridSpacer() }
        }
    }
}