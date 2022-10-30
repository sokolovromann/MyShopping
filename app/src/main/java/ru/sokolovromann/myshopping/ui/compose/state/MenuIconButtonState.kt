package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MenuIconButtonState<M> {

    var currentData by mutableStateOf(MenuIconButtonData<M>())
        private set

    fun showButton(icon: IconData) {
        currentData = MenuIconButtonData(icon = icon)
    }

    fun showMenu(menu: M) {
        currentData = currentData.copy(menu = menu)
    }

    fun hideMenu() {
        currentData = currentData.copy(menu = null)
    }

    fun isMenuShowing(): Boolean {
        return currentData.menu != null
    }

    fun isMenuHiding(): Boolean {
        return currentData.menu == null
    }
}

data class MenuIconButtonData<M>(
    val icon: IconData = IconData.OnSurface,
    val menu: M? = null
)