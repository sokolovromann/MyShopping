package ru.sokolovromann.myshopping.core.domain.model

sealed class GroupCartsByStatus {

    data class CompletedFirst(val displayEmpty: Boolean) : GroupCartsByStatus()

    data class ActiveFirst(val displayEmpty: Boolean) : GroupCartsByStatus()

    data class HideCompleted(val displayEmpty: Boolean) : GroupCartsByStatus()

    data class DoNotGroup(val displayEmpty: Boolean) : GroupCartsByStatus()

    fun isDisplayEmpty(): Boolean = when (this) {
        is CompletedFirst -> displayEmpty
        is ActiveFirst -> displayEmpty
        is HideCompleted -> displayEmpty
        is DoNotGroup -> displayEmpty
    }
}