package ru.sokolovromann.myshopping.core.domain.model

data class FilteredFabricsByType(
    val quantities: Collection<Fabric>,
    val unitPrices: Collection<Fabric>,
    val discounts: Collection<Fabric>,
    val taxes: Collection<Fabric>,
    val cost: Collection<Fabric>,
    val manufacturers: Collection<Fabric>,
    val brands: Collection<Fabric>,
    val sizes: Collection<Fabric>,
    val colors: Collection<Fabric>
)