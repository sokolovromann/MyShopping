package ru.sokolovromann.myshopping.ui.viewmodel

interface ViewModelEvent<E> {

    fun onEvent(event: E)
}