package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MenuButtonState<M> {

    var currentData by mutableStateOf(MenuButtonData<M>())
        private set

    fun showButton(text: TextData) {
        currentData = MenuButtonData(text = text)
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

data class MenuButtonData<M>(
    val text: TextData = TextData.Body,
    val menu: M? = null
)