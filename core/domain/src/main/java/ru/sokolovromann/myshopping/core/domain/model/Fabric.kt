package ru.sokolovromann.myshopping.core.domain.model

data class Fabric(
    val uid: UID,
    val directory: FabricDirectory,
    val created: TimeInMillis,
    val lastModified: TimeInMillis,
    val value: FabricValue,
    val used: Int
)