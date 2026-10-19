package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class AddEditProductSettingsItems(
    val afterTappingEnter: SettingsItem<AfterTappingByProductEnter> = SettingsItem(UserPreferencesDefaults.AddEditProduct.afterTappingByEnter),
    val afterAdding: SettingsItem<AfterAddingProduct> = SettingsItem(UserPreferencesDefaults.AddEditProduct.afterAdding),
    val afterEditing: SettingsItem<AfterEditingProduct> = SettingsItem(UserPreferencesDefaults.AddEditProduct.afterEditing),
    val tax: SettingsItem<String> = SettingsItem("")
)