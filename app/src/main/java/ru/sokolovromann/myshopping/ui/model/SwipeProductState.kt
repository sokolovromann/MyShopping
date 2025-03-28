package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.SwipeProduct

class SwipeProductState {

    var swipeProductLeftValue: SelectedValue<SwipeProduct> by mutableStateOf(SelectedValue(SwipeProduct.DefaultValue))
        private set

    var swipeProductLeftExpanded: Boolean by mutableStateOf(false)
        private set

    var swipeProductRightValue: SelectedValue<SwipeProduct> by mutableStateOf(SelectedValue(SwipeProduct.DefaultValue))
        private set

    var swipeProductRightExpanded: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig) {
        val userPreferences = appConfig.userPreferences

        swipeProductLeftValue = toSwipeProductValue(userPreferences.swipeProductLeft)
        swipeProductLeftExpanded = false
        swipeProductRightValue = toSwipeProductValue(userPreferences.swipeProductRight)
        swipeProductRightExpanded = false
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun onSelectSwipeProductLeft(expanded: Boolean) {
        swipeProductLeftExpanded = expanded
    }

    fun onSwipeProductLeftSelected(swipeProduct: SwipeProduct) {
        swipeProductLeftValue = toSwipeProductValue(swipeProduct)
        swipeProductLeftExpanded = false
    }

    fun onSelectSwipeProductRight(expanded: Boolean) {
        swipeProductRightExpanded = expanded
    }

    fun onSwipeProductRightSelected(swipeProduct: SwipeProduct) {
        swipeProductRightValue = toSwipeProductValue(swipeProduct)
        swipeProductRightExpanded = false
    }

    private fun toSwipeProductValue(swipeProduct: SwipeProduct): SelectedValue<SwipeProduct> {
        return SelectedValue(
            selected = swipeProduct,
            text = when (swipeProduct) {
                SwipeProduct.DISABLED -> UiString.FromResources(R.string.swipeProduct_action_disabled)
                SwipeProduct.EDIT -> UiString.FromResources(R.string.swipeProduct_action_edit)
                SwipeProduct.DELETE -> UiString.FromResources(R.string.swipeProduct_action_delete)
                SwipeProduct.COMPLETE -> UiString.FromResources(R.string.swipeProduct_action_complete)
            }
        )
    }
}