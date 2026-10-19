package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.GroupCartsByStatus
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class CartsSettingsItems(
    val groupByStatus: SettingsItem<GroupCartsByStatus> = SettingsItem(UserPreferencesDefaults.Carts.groupByStatus),
    val afterAdding: SettingsItem<AfterAddingCart> = SettingsItem(UserPreferencesDefaults.Carts.afterAdding),
    val afterCompleting: SettingsItem<AfterCompletingCart> = SettingsItem(UserPreferencesDefaults.Carts.afterCompleting),
    val afterArchiving: SettingsItem<AfterArchivingCart> = SettingsItem(UserPreferencesDefaults.Carts.afterArchiving),
    val afterTappingCheckbox: SettingsItem<AfterTappingByCartCheckbox> = SettingsItem(UserPreferencesDefaults.Carts.afterTappingByCheckbox),
    val checkboxColor: SettingsItem<CheckboxColor> = SettingsItem(UserPreferencesDefaults.Carts.checkboxColor),
    val swipeLeft: SettingsItem<SwipeCart.Left> = SettingsItem(UserPreferencesDefaults.Carts.swipeLeft),
    val swipeRight: SettingsItem<SwipeCart.Right> = SettingsItem(UserPreferencesDefaults.Carts.swipeRight),
    val deletionFromTrash: SettingsItem<DeletionCartFromTrash> = SettingsItem(UserPreferencesDefaults.Carts.deletionFromTrash)
)