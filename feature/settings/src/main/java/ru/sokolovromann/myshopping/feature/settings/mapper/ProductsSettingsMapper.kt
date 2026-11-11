package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
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
            swipeLeft = swipeLeft,
            swipeRight = swipeRight
        )
    }
}