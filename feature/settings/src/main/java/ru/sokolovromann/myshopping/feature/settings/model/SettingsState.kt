package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val isLoading: Boolean = true,
    val generalItems: GeneralSettingsItems = GeneralSettingsItems(),
    val cartsItems: CartsSettingsItems = CartsSettingsItems(),
    val productsItems: ProductsSettingsItems = ProductsSettingsItems(),
    val productsWidgetItems: ProductsWidgetSettingsItems = ProductsWidgetSettingsItems(),
    val addEditProductItems: AddEditProductSettingsItems = AddEditProductSettingsItems()
)