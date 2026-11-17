package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiCartsPreferences(
    val afterAdding: UiPreference<AfterAddingCart> = UiPreference(UserPreferencesDefaults.Carts.afterAdding),
    val afterCompleting: UiPreference<AfterCompletingCart> = UiPreference(UserPreferencesDefaults.Carts.afterCompleting),
    val afterArchiving: UiPreference<AfterArchivingCart> = UiPreference(UserPreferencesDefaults.Carts.afterArchiving),
    val swipeLeft: UiPreference<SwipeCart.Left> = UiPreference(UserPreferencesDefaults.Carts.swipeLeft),
    val swipeRight: UiPreference<SwipeCart.Right> = UiPreference(UserPreferencesDefaults.Carts.swipeRight),
    val deletionFromTrash: UiPreference<DeletionCartFromTrash> = UiPreference(UserPreferencesDefaults.Carts.deletionFromTrash)
)