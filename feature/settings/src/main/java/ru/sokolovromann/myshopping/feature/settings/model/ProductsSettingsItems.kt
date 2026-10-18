package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class ProductsSettingsItems(
    val addingMode: SettingsItem<ProductsAddingMode> = SettingsItem(UserPreferencesDefaults.Products.addingMode),
    val strikethroughCompleted: SettingsItem<StrikethroughCompletedProducts> = SettingsItem(UserPreferencesDefaults.Products.strikethroughCompleted),
    val afterCompleting: SettingsItem<AfterCompletingProduct> = SettingsItem(UserPreferencesDefaults.Products.afterCompleting),
    val afterTappingCheckbox: SettingsItem<AfterTappingByProductCheckbox> = SettingsItem(UserPreferencesDefaults.Products.afterTappingByCheckbox),
    val checkboxColor: SettingsItem<CheckboxColor> = SettingsItem(UserPreferencesDefaults.Products.checkboxColor),
    val afterTappingItem: SettingsItem<AfterTappingByProductItem> = SettingsItem(UserPreferencesDefaults.Products.afterTappingByItem),
    val swipeLeft: SettingsItem<SwipeProduct.Left> = SettingsItem(UserPreferencesDefaults.Products.swipeLeft),
    val swipeRight: SettingsItem<SwipeProduct.Right> = SettingsItem(UserPreferencesDefaults.Products.swipeRight)
)