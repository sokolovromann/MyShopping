package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.AppPreferences

class MainState {

    var loading by mutableStateOf(false)
        private set

    var nightTheme by mutableStateOf(false)
        private set

    var shoppingUid: String? by mutableStateOf(null)
        private set

    fun showLoading() {
        loading = true
    }

    fun hideLoading() {
        loading = false
    }

    fun showProducts(shoppingUid: String) {
        this.shoppingUid = shoppingUid
    }

    fun clearShoppingUid() {
        shoppingUid = null
    }

    fun applyPreferences(preferences: AppPreferences) {
        nightTheme = preferences.nightTheme
    }
}