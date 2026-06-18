package ru.sokolovromann.myshopping.core.domain.model

import java.math.BigDecimal

sealed class FabricValue {

    data class QuantityType(val data: ProductQuantity) : FabricValue()

    data class UnitPriceType(val data: BigDecimal) : FabricValue()

    data class DiscountType(val data: ProductDiscount) : FabricValue()

    data class TaxType(val data: Tax) : FabricValue()

    data class CostType(val data: BigDecimal) : FabricValue()

    data class ManufacturerType(val data: String) : FabricValue()

    data class BrandType(val data: String) : FabricValue()

    data class SizeType(val data: String) : FabricValue()

    data class ColorType(val data: String) : FabricValue()

    data object NoData : FabricValue()
}