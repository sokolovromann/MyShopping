package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MenuButtonState<M> {

    var currentData by mutableStateOf(MenuButtonData<M>())
        private set

    fun showButton(text: TextData, menu: M) {
        currentData = MenuButtonData(
            text = text,
            menu = menu
        )
    }

    fun showMenu() {
        currentData = currentData.copy(expandedMenu = true)
    }

    fun changeText(text: TextData) {
        currentData = currentData.copy(
            text = text
        )
    }

    fun hideButton() {
        currentData = currentData.copy(
            text = TextData()
        )
    }

    fun hideMenu() {
        currentData = currentData.copy(expandedMenu = false)
    }
}

data class MenuButtonData<M>(
    val text: TextData = TextData(),
    val menu: M? = null,
    val expandedMenu: Boolean = false,
)