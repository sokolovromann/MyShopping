package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.ProductsSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

class ProductsSettingsMapper @Inject constructor() {

    fun toItems(preferences: ProductsPreferences): ProductsSettingsItems {
        val addingMode = SettingsItem(
            selected = preferences.addingMode,
            text = when (preferences.addingMode) {
                ProductsAddingMode.Simple ->
                    UiText.FromResources(R.string.settings_text_products_adding_mode_simple)
                ProductsAddingMode.Advanced ->
                    UiText.FromResources(R.string.settings_text_products_adding_mode_advanced)
            }
        )
        val strikethroughCompleted = SettingsItem(
            selected = preferences.strikethroughCompleted,
            text = when (preferences.strikethroughCompleted) {
                StrikethroughCompletedProducts.On ->
                    UiText.FromResources(R.string.settings_text_strikethrough_products_completed_on)
                StrikethroughCompletedProducts.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_off)
            }
        )
        val afterCompleting = SettingsItem(
            selected = preferences.afterCompleting,
            text = when (preferences.afterCompleting) {
                AfterCompletingProduct.EditProduct ->
                    UiText.FromResources(R.string.settings_text_after_completing_product_edit)
                AfterCompletingProduct.DeleteProduct ->
                    UiText.FromResources(R.string.settings_text_after_completing_product_delete)
                AfterCompletingProduct.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_completing_product_do_nothing)
            }
        )
        val afterTappingCheckbox = SettingsItem(
            selected = preferences.afterTappingByCheckbox,
            text = when (preferences.afterTappingByCheckbox) {
                AfterTappingByProductCheckbox.ChangeProductStatus ->
                    UiText.FromResources(R.string.settings_text_after_tapping_product_checkbox_change_status)
                AfterTappingByProductCheckbox.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_tapping_product_checkbox_do_nothing)
            }
        )
        val checkboxColor = SettingsItem(
            selected = preferences.checkboxColor,
            text = when (preferences.checkboxColor) {
                CheckboxColor.RedOrGreen ->
                    UiText.FromResources(R.string.settings_text_product_checkbox_color_red_green)
                CheckboxColor.RedOrGray ->
                    UiText.FromResources(R.string.settings_text_product_checkbox_color_red_grey)
                CheckboxColor.Gray ->
                    UiText.FromResources(R.string.settings_text_product_checkbox_color_grey)
            }
        )
        val afterTappingItem = SettingsItem(
            selected = preferences.afterTappingByItem,
            text = when (preferences.afterTappingByItem) {
                AfterTappingByProductItem.ChangeProductStatus ->
                    UiText.FromResources(R.string.settings_text_after_tapping_product_item_change_status)
                AfterTappingByProductItem.DoNothing ->
                    UiText.FromResources(R.string.settings_text_after_tapping_product_item_do_nothing)
            }
        )
        val swipeLeft = SettingsItem(
            selected = preferences.swipeLeft,
            text = when (preferences.swipeLeft.actionName) {
                SwipeProductActionName.EditProduct ->
                    UiText.FromResources(R.string.settings_text_swipe_product_edit)
                SwipeProductActionName.DeleteProduct ->
                    UiText.FromResources(R.string.settings_text_swipe_product_delete)
                SwipeProductActionName.ChangeProductStatusToCompletedOrActive ->
                    UiText.FromResources(R.string.settings_text_swipe_product_change_status)
                SwipeProductActionName.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_product_off)
            }
        )
        val swipeRight = SettingsItem(
            selected = preferences.swipeRight,
            text = when (preferences.swipeRight.actionName) {
                SwipeProductActionName.EditProduct ->
                    UiText.FromResources(R.string.settings_text_swipe_product_edit)
                SwipeProductActionName.DeleteProduct ->
                    UiText.FromResources(R.string.settings_text_swipe_product_delete)
                SwipeProductActionName.ChangeProductStatusToCompletedOrActive ->
                    UiText.FromResources(R.string.settings_text_swipe_product_change_status)
                SwipeProductActionName.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_product_off)
            }
        )
        return ProductsSettingsItems(
            addingMode = addingMode,
            strikethroughCompleted = strikethroughCompleted,
            afterCompleting = afterCompleting,
            afterTappingCheckbox = afterTappingCheckbox,
            checkboxColor = checkboxColor,
            afterTappingItem = afterTappingItem,
            swipeLeft = swipeLeft,
            swipeRight = swipeRight
        )
    }
}