package ru.sokolovromann.myshopping.core.domain.model

data class CartReminder(
    val time: TimeInMillis,
    val repeat: RepeatCartReminder
)