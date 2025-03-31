package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.SwipeShopping

class SwipeShoppingState {

    var swipeShoppingLeftValue: SelectedValue<SwipeShopping> by mutableStateOf(SelectedValue(SwipeShopping.DefaultValue))
        private set

    var swipeShoppingLeftExpanded: Boolean by mutableStateOf(false)
        private set

    var swipeShoppingRightValue: SelectedValue<SwipeShopping> by mutableStateOf(SelectedValue(SwipeShopping.DefaultValue))
        private set

    var swipeShoppingRightExpanded: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig) {
        val userPreferences = appConfig.userPreferences

        swipeShoppingLeftValue = toSwipeProductValue(userPreferences.swipeShoppingLeft)
        swipeShoppingLeftExpanded = false
        swipeShoppingRightValue = toSwipeProductValue(userPreferences.swipeShoppingRight)
        swipeShoppingRightExpanded = false
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun onSelectSwipeShoppingLeft(expanded: Boolean) {
        swipeShoppingLeftExpanded = expanded
    }

    fun onSwipeShoppingLeftSelected(swipeShopping: SwipeShopping) {
        swipeShoppingLeftValue = toSwipeProductValue(swipeShopping)
        swipeShoppingLeftExpanded = false
    }

    fun onSelectSwipeShoppingRight(expanded: Boolean) {
        swipeShoppingRightExpanded = expanded
    }

    fun onSwipeShoppingRightSelected(swipeShopping: SwipeShopping) {
        swipeShoppingRightValue = toSwipeProductValue(swipeShopping)
        swipeShoppingRightExpanded = false
    }

    private fun toSwipeProductValue(swipeShopping: SwipeShopping): SelectedValue<SwipeShopping> {
        return SelectedValue(
            selected = swipeShopping,
            text = when (swipeShopping) {
                SwipeShopping.DISABLED -> UiString.FromResources(R.string.swipeShopping_action_disabled)
                SwipeShopping.ARCHIVE -> UiString.FromResources(R.string.swipeShopping_action_archive)
                SwipeShopping.DELETE -> UiString.FromResources(R.string.swipeShopping_action_delete)
                SwipeShopping.COMPLETE -> UiString.FromResources(R.string.swipeShopping_action_completed)
            }
        )
    }
}