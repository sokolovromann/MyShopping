package ru.sokolovromann.myshopping.data.repository.model

sealed class AppOpenHelper {

    object Open : AppOpenHelper()

    object Create : AppOpenHelper()

    object Migrate : AppOpenHelper()

    data class Error(val exception: Exception) : AppOpenHelper()

    override fun toString(): String = when (this) {
        is Create -> "Create"
        is Open -> "Open"
        is Migrate -> "Migrate"
        is Error -> "Error: ${exception.message}"
    }
}