package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.SwipeCartActionName
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.CartsSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

class CartsSettingsMapper @Inject constructor() {

    fun toItems(preferences: CartsPreferences): CartsSettingsItems {
        val afterAdding = SettingsItem(
            selected = preferences.afterAdding,
            text = when (preferences.afterAdding) {
                AfterAddingCart.OpenProductsScreen ->
                    UiText.FromResources(R.string.settings_text_after_adding_cart_open_products)
                AfterAddingCart.OpenAddNameScreen ->
                    UiText.FromResources(R.string.settings_text_after_adding_cart_open_add_name)
                AfterAddingCart.OpenAddProductScreen ->
                    UiText.FromResources(R.string.settings_text_after_adding_cart_open_products)
                AfterAddingCart.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_adding_cart_do_nothing)
            }
        )
        val afterCompleting = SettingsItem(
            selected = preferences.afterCompleting,
            text = when (preferences.afterCompleting) {
                AfterCompletingCart.ArchiveCart ->
                    UiText.FromResources(R.string.settings_text_after_completing_cart_archive_cart)
                AfterCompletingCart.DeleteCart ->
                    UiText.FromResources(R.string.settings_text_after_completing_cart_delete_cart)
                AfterCompletingCart.DeleteProducts ->
                    UiText.FromResources(R.string.settings_text_after_completing_cart_delete_products)
                AfterCompletingCart.DeleteCartAndProducts ->
                    UiText.FromResources(R.string.settings_text_after_completing_cart_delete_cart_products)
                AfterCompletingCart.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_completing_cart_do_nothing)
            }
        )
        val afterArchiving = SettingsItem(
            selected = preferences.afterArchiving,
            text = when (preferences.afterArchiving) {
                AfterArchivingCart.ChangeCartStatusToCompleted ->
                    UiText.FromResources(R.string.settings_text_after_archiving_cart_change_to_completed)
                AfterArchivingCart.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_archiving_cart_do_nothing)
            }
        )
        val afterTappingCheckbox = SettingsItem(
            selected = preferences.afterTappingByCheckbox,
            text = when (preferences.afterTappingByCheckbox) {
                AfterTappingByCartCheckbox.ChangeCartStatus ->
                    UiText.FromResources(R.string.settings_text_after_tapping_cart_checkbox_change_status)
                AfterTappingByCartCheckbox.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_tapping_cart_checkbox_do_nothing)
            }
        )
        val checkboxColor = SettingsItem(
            selected = preferences.checkboxColor,
            text = when (preferences.checkboxColor) {
                CheckboxColor.RedOrGreen ->
                    UiText.FromResources(R.string.settings_text_cart_checkbox_color_red_green)
                CheckboxColor.RedOrGray ->
                    UiText.FromResources(R.string.settings_text_cart_checkbox_color_red_grey)
                CheckboxColor.Gray ->
                    UiText.FromResources(R.string.settings_text_cart_checkbox_color_grey)
            }
        )
        val swipeLeft = SettingsItem(
            selected = preferences.swipeLeft,
            text = when (preferences.swipeLeft.actionName) {
                SwipeCartActionName.ArchiveOrUnarchiveCart ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_archive_unarchive_cart)
                SwipeCartActionName.DeleteOrRestoreCart ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_delete_restore_cart)
                SwipeCartActionName.DeleteProducts ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_delete_products)
                SwipeCartActionName.ChangeCartStatusToCompletedOrActive ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_change_cart_status)
                SwipeCartActionName.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_off)
            }
        )
        val swipeRight = SettingsItem(
            selected = preferences.swipeRight,
            text = when (preferences.swipeRight.actionName) {
                SwipeCartActionName.ArchiveOrUnarchiveCart ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_archive_unarchive_cart)
                SwipeCartActionName.DeleteOrRestoreCart ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_delete_restore_cart)
                SwipeCartActionName.DeleteProducts ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_delete_products)
                SwipeCartActionName.ChangeCartStatusToCompletedOrActive ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_change_cart_status)
                SwipeCartActionName.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_off)
            }
        )
        val deletionFromTrash = SettingsItem(
            selected = preferences.deletionFromTrash,
            text = when (preferences.deletionFromTrash) {
                DeletionCartFromTrash.After7Days ->
                    UiText.FromResources(R.string.settings_text_deletion_cart_from_trash_after_7_days)
                DeletionCartFromTrash.DoNotDelete ->
                    UiText.FromResources(R.string.settings_text_deletion_cart_from_trash_do_not_delete)
            }
        )
        return CartsSettingsItems(
            afterAdding = afterAdding,
            afterCompleting = afterCompleting,
            afterArchiving = afterArchiving,
            afterTappingCheckbox = afterTappingCheckbox,
            checkboxColor = checkboxColor,
            swipeLeft = swipeLeft,
            swipeRight = swipeRight,
            deletionFromTrash = deletionFromTrash
        )
    }
}