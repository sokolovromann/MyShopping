package ru.sokolovromann.myshopping.core.domain.model

data class BackupValue(
    val carts: Collection<Cart>,
    val products: Collection<Product>,
    val suggestions: Collection<Suggestion>,
    val fabrics: Collection<Fabric>
)