package ru.sokolovromann.myshopping.data.repository.model

interface AppNumber {

    fun getDisplayValue(): String

    fun isEmpty(): Boolean

    fun isNotEmpty(): Boolean
}