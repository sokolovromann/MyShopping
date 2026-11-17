package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiAddEditProductPreferences(
    val afterTappingEnter: UiPreference<AfterTappingByProductEnter> = UiPreference(UserPreferencesDefaults.AddEditProduct.afterTappingByEnter),
    val afterAdding: UiPreference<AfterAddingProduct> = UiPreference(UserPreferencesDefaults.AddEditProduct.afterAdding),
    val afterEditing: UiPreference<AfterEditingProduct> = UiPreference(UserPreferencesDefaults.AddEditProduct.afterEditing),
    val tax: UiPreference<String> = UiPreference("")
)