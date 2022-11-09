package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ItemMenuState<M> {

    var currentData by mutableStateOf(ItemMenuData<M>())
        private set

    fun setMenu(menu: M) {
        currentData = ItemMenuData(menu = menu)
    }

    fun showMenu(itemUid: String) {
        currentData = currentData.copy(itemUid = itemUid)
    }

    fun hideMenu() {
        currentData = currentData.copy(itemUid = null)
    }
}

data class ItemMenuData<M>(
    val menu: M? = null,
    val itemUid: String? = null
)