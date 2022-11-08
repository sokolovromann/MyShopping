package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MenuIconButtonState<M> {

    var currentData by mutableStateOf(MenuIconButtonData<M>())
        private set

    fun showButton(icon: IconData, menu: M) {
        currentData = MenuIconButtonData(
            icon = icon,
            menu = menu
        )
    }

    fun showMenu() {
        currentData = currentData.copy(expandedMenu = true)
    }

    fun hideButton() {
        currentData = currentData.copy(
            icon = IconData()
        )
    }

    fun hideMenu() {
        currentData = currentData.copy(expandedMenu = false)
    }
}

data class MenuIconButtonData<M>(
    val icon: IconData = IconData(),
    val menu: M? = null,
    val expandedMenu: Boolean = false,
)