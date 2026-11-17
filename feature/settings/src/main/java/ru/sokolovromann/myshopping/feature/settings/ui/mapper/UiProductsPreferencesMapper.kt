package ru.sokolovromann.myshopping.feature.settings.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiPreference
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiProductsPreferences

class UiProductsPreferencesMapper @Inject constructor() {

    fun toPreferences(preferences: ProductsPreferences): UiProductsPreferences {
        val addingMode = UiPreference(
            selected = preferences.addingMode,
            text = when (preferences.addingMode) {
                ProductsAddingMode.Simple ->
                    UiText.FromResources(R.string.settings_text_products_adding_mode_simple)
                ProductsAddingMode.Advanced ->
                    UiText.FromResources(R.string.settings_text_products_adding_mode_advanced)
            }
        )
        val strikethroughCompleted = UiPreference(
            selected = preferences.strikethroughCompleted,
            text = when (preferences.strikethroughCompleted) {
                StrikethroughCompletedProducts.On ->
                    UiText.FromResources(R.string.settings_text_strikethrough_products_completed_on)
                StrikethroughCompletedProducts.Off ->
                    UiText.FromResources(R.string.settings_text_swipe_cart_off)
            }
        )
        val afterCompleting = UiPreference(
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
        val swipeLeft = UiPreference(
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
        val swipeRight = UiPreference(
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
        return UiProductsPreferences(addingMode, strikethroughCompleted,
            afterCompleting, swipeLeft, swipeRight)
    }
}